/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-21 上午3:36
 */

package com.weilylab.xhuschedule.fragment

import android.content.Intent
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Student
import com.weilylab.xhuschedule.classes.Update
import com.weilylab.xhuschedule.interfaces.CommonService
import com.weilylab.xhuschedule.listener.FeedBackListener
import com.weilylab.xhuschedule.service.DownloadService
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.XhuFileUtil
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.fileUtil.FileUtil
import java.io.File
import java.io.InputStreamReader
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
            val editText = EditText(activity)
            editText.hint = "请输入您的建议"
            AlertDialog.Builder(activity)
                    .setTitle(R.string.operation_feedback)
                    .setView(editText)
                    .setPositiveButton(android.R.string.ok, { dialog, _ ->
                        loadingDialog.show()
                        val studentList = XhuFileUtil.getArrayFromFile(File(activity.filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java)
                        var mainStudent: Student? = (0 until studentList.size)
                                .firstOrNull { studentList[it].isMain }
                                ?.let { studentList[it] }
                        if (mainStudent == null)
                            mainStudent = studentList[0]
                        mainStudent.feedback(activity, editText.text.toString(), object : FeedBackListener {
                            override fun error(rt: Int, e: Throwable) {
                                e.printStackTrace()
                                loadingDialog.dismiss()
                                Toast.makeText(activity, activity.getString(R.string.hint_feedback_error, rt, e.message), Toast.LENGTH_LONG)
                                        .show()
                            }

                            override fun done(rt: Int) {
                                loadingDialog.dismiss()
                                dialog.dismiss()
                                Toast.makeText(activity, R.string.hint_feedback, Toast.LENGTH_SHORT)
                                        .show()
                            }

                            override fun doInThread() {
                            }
                        })
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
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
            ScheduleHelper.phpRetrofit
                    .create(CommonService::class.java)
                    .checkUpdateCall(getString(R.string.app_version_code).toInt())
                    .subscribeOn(Schedulers.newThread())
                    .unsubscribeOn(Schedulers.newThread())
                    .map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), Update::class.java) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Update> {
                        private lateinit var update: Update

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onError(e: Throwable) {
                            loadingDialog.dismiss()
                            e.printStackTrace()
                            if (e is UnknownHostException)
                                Toast.makeText(activity, R.string.error_network, Toast.LENGTH_SHORT)
                                        .show()
                            else
                                Toast.makeText(activity, "请求出错：" + e.message, Toast.LENGTH_SHORT)
                                        .show()
                        }

                        override fun onComplete() {
                            loadingDialog.dismiss()
                            if (update.code == 1) {
                                val version = update.version
                                val title = activity.getString(R.string.update_notification_title, activity.getString(R.string.app_version_name), version.versionName)
                                val content = activity.getString(R.string.update_notification_content, FileUtil.FormatFileSize(version.apkSize), FileUtil.FormatFileSize(version.patchSize))
                                val bigText = content + "\n" + activity.getString(R.string.update_notification_big_text, version.updateLog)
                                val builder = AlertDialog.Builder(activity)
                                        .setTitle(title)
                                        .setMessage(bigText)
                                        .setPositiveButton(R.string.action_download_apk, { _, _ ->
                                            val downloadAPKIntent = Intent(activity, DownloadService::class.java)
                                            downloadAPKIntent.putExtra("type", "apk")
                                            downloadAPKIntent.putExtra("fileName", version.versionAPK)
                                            activity.startService(downloadAPKIntent)
                                        })
                                if (version.lastVersion == activity.getString(R.string.app_version_code).toInt())
                                    builder.setNegativeButton(R.string.action_download_patch, { _, _ ->
                                        val downloadPatchIntent = Intent(activity, DownloadService::class.java)
                                        downloadPatchIntent.putExtra("type", "patch")
                                        downloadPatchIntent.putExtra("fileName", version.lastVersionPatch)
                                        activity.startService(downloadPatchIntent)
                                    })
                                builder.show()
                            } else
                                Toast.makeText(activity, update.message, Toast.LENGTH_SHORT)
                                        .show()
                        }

                        override fun onNext(update: Update) {
                            this.update = update
                        }
                    })
            true
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}