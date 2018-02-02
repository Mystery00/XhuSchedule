/*
 * Created by Mystery0 on 18-1-12 下午8:52.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                  =====================================================
 *                  =                                                   =
 *                  =                       _oo0oo_                     =
 *                  =                      o8888888o                    =
 *                  =                      88" . "88                    =
 *                  =                      (| -_- |)                    =
 *                  =                      0\  =  /0                    =
 *                  =                    ___/`---'\___                  =
 *                  =                  .' \\|     |# '.                 =
 *                  =                 / \\|||  :  |||# \                =
 *                  =                / _||||| -:- |||||- \              =
 *                  =               |   | \\\  -  #/ |   |              =
 *                  =               | \_|  ''\---/''  |_/ |             =
 *                  =               \  .-\__  '-'  ___/-. /             =
 *                  =             ___'. .'  /--.--\  `. .'___           =
 *                  =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                  =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                  =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                  =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                  =                       `=---='                     =
 *                  =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                  =                                                   =
 *                  =               佛祖保佑         永无BUG              =
 *                  =                                                   =
 *                  =====================================================
 *
 * Last modified 18-1-12 下午8:51
 */

package com.weilylab.xhuschedule.fragment

import android.content.Intent
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.classes.baseClass.Version
import com.weilylab.xhuschedule.listener.FeedBackListener
import com.weilylab.xhuschedule.service.DownloadService
import com.weilylab.xhuschedule.util.FirebaseConstant
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.XhuFileUtil
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import vip.mystery0.tools.fileUtil.FileUtil
import java.io.File
import java.net.UnknownHostException

/**
 * Created by myste.
 */
class InfoSettingsFragment : PreferenceFragment() {
    private lateinit var loadingDialog: ZLoadingDialog
    private lateinit var autoCheckUpdatePreference: SwitchPreference
    private lateinit var autoCheckLogPreference: SwitchPreference
    private lateinit var feedbackPreference: Preference
    private lateinit var weixinPreference: Preference
    private lateinit var updateLogPreference: Preference
    private lateinit var checkUpdatePreference: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_info)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        loadingDialog = ZLoadingDialog(activity)
                .setLoadingBuilder(Z_TYPE.SEARCH_PATH)
                .setHintText(getString(R.string.hint_dialog_check_update))
                .setHintTextSize(16F)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(ContextCompat.getColor(activity, R.color.colorAccent))
                .setHintTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
        autoCheckUpdatePreference = findPreference(getString(R.string.key_auto_check_update)) as SwitchPreference
        autoCheckLogPreference = findPreference(getString(R.string.key_auto_check_log)) as SwitchPreference
        feedbackPreference = findPreference(getString(R.string.key_feedback))
        weixinPreference = findPreference(getString(R.string.key_weixin))
        updateLogPreference = findPreference(getString(R.string.key_update_log))
        checkUpdatePreference = findPreference(getString(R.string.key_check_update))
        autoCheckUpdatePreference.isChecked = Settings.autoCheckUpdate
        autoCheckLogPreference.isChecked = Settings.autoCheckLog
        autoCheckUpdatePreference.setOnPreferenceChangeListener { _, _ ->
            Settings.autoCheckUpdate = !autoCheckUpdatePreference.isChecked
            true
        }
        autoCheckLogPreference.setOnPreferenceChangeListener { _, _ ->
            Settings.autoCheckLog = !autoCheckLogPreference.isChecked
            true
        }
        feedbackPreference.setOnPreferenceClickListener {
            val loadingDialog = ZLoadingDialog(activity)
                    .setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)
                    .setHintText(activity.getString(R.string.hint_dialog_feedback))
                    .setHintTextSize(16F)
                    .setCanceledOnTouchOutside(false)
                    .setLoadingColor(ContextCompat.getColor(activity, R.color.colorAccent))
                    .setHintTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
                    .create()
            val layout = View.inflate(activity, R.layout.dialog_feedback, null)
            val emailInput: TextInputLayout = layout.findViewById(R.id.input_email)
            val textInput: TextInputLayout = layout.findViewById(R.id.input_text)
            val dialog = AlertDialog.Builder(activity)
                    .setTitle(R.string.operation_feedback)
                    .setView(layout)
                    .setPositiveButton(android.R.string.ok, null)
                    .setNegativeButton(android.R.string.cancel, null)
                    .create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (emailInput.editText!!.text.toString().isEmpty() || textInput.editText!!.text.toString().isEmpty()) {
                    Toast.makeText(activity, R.string.hint_feedback_empty, Toast.LENGTH_SHORT)
                            .show()
                } else {
                    loadingDialog.show()
                    val studentList = XhuFileUtil.getArrayFromFile(File(activity.filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java)
                    var mainStudent: Student? = (0 until studentList.size)
                            .firstOrNull { studentList[it].isMain }
                            ?.let { studentList[it] }
                    if (mainStudent == null)
                        mainStudent = studentList[0]
                    mainStudent.feedback(activity, emailInput.editText!!.text.toString(), textInput.editText!!.text.toString(), object : FeedBackListener {
                        override fun error(rt: Int, e: Throwable) {
                            e.printStackTrace()
                            loadingDialog.dismiss()
                            Toast.makeText(activity, getString(R.string.hint_feedback_error, rt, e.message), Toast.LENGTH_LONG)
                                    .show()
                        }

                        override fun done(rt: Int) {
                            loadingDialog.dismiss()
                            dialog.dismiss()
                            Toast.makeText(activity, R.string.hint_feedback, Toast.LENGTH_SHORT)
                                    .show()
                        }
                    })
                }
            }
            true
        }
        weixinPreference.setOnPreferenceClickListener {
            AlertDialog.Builder(activity)
                    .setTitle(R.string.title_weixin)
                    .setView(R.layout.dialog_weixin)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            true
        }
        updateLogPreference.setOnPreferenceClickListener {
            var message = ""
            resources.getStringArray(R.array.update_list)
                    .forEach { message += it + '\n' }
            AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.dialog_title_update_log, getString(R.string.app_version_name) + '-' + getString(R.string.app_version_code)))
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            true
        }
        checkUpdatePreference.setOnPreferenceClickListener {
            loadingDialog.show()
            val reference = FirebaseDatabase.getInstance().getReference(FirebaseConstant.LATEST_VERSION)
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError?) {
                    if (databaseError == null)
                        return
                    val exception = databaseError.toException()
                    exception.printStackTrace()
                    if (exception is UnknownHostException)
                        Toast.makeText(activity, R.string.error_network, Toast.LENGTH_LONG)
                                .show()
                    else
                        Toast.makeText(activity, "请求出错：" + exception.message, Toast.LENGTH_LONG)
                                .show()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                    val version = dataSnapshot?.getValue(Version::class.java)
                    if (version == null) {
                        Toast.makeText(activity, R.string.error_real_time_database_null, Toast.LENGTH_LONG)
                                .show()
                        return
                    }
                    if (version.versionCode > getString(R.string.app_version_code).toInt()) {
                        val title = activity.getString(R.string.dialog_update_title, activity.getString(R.string.app_version_name), version.versionName)
                        val content = activity.getString(R.string.dialog_update_content, FileUtil.FormatFileSize(version.apkSize), FileUtil.FormatFileSize(version.patchSize))
                        val text = content + "\n" + activity.getString(R.string.dialog_update_text, version.updateLog)
                        val builder = AlertDialog.Builder(activity)
                                .setTitle(title)
                                .setMessage(text)
                                .setPositiveButton(R.string.action_download_apk, { _, _ ->
                                    val downloadAPKIntent = Intent(activity, DownloadService::class.java)
                                    downloadAPKIntent.putExtra("type", "apk")
                                    downloadAPKIntent.putExtra("fileName", version.apkDownloadUrl)
                                    activity.startService(downloadAPKIntent)
                                })
                        if (version.lastVersionCode == activity.getString(R.string.app_version_code).toInt())
                            builder.setNegativeButton(R.string.action_download_patch, { _, _ ->
                                val downloadPatchIntent = Intent(activity, DownloadService::class.java)
                                downloadPatchIntent.putExtra("type", "patch")
                                downloadPatchIntent.putExtra("fileName", version.patchDownloadUrl)
                                activity.startService(downloadPatchIntent)
                            })
                        builder.show()
                    } else
                        Toast.makeText(activity, R.string.hint_dialog_check_update_latest, Toast.LENGTH_SHORT)
                                .show()
                }
            })
            true
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}