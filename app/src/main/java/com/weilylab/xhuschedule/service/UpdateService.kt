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

package com.weilylab.xhuschedule.service

import android.app.IntentService
import android.content.Intent
import android.support.v7.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.Update
import com.weilylab.xhuschedule.classes.baseClass.Version
import com.weilylab.xhuschedule.interfaces.PhpService
import com.weilylab.xhuschedule.util.APPActivityManager
import com.weilylab.xhuschedule.util.FirebaseConstant
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.Settings
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.fileUtil.FileUtil
import vip.mystery0.tools.logs.Logs
import java.io.InputStreamReader

class UpdateService : IntentService("PhpService") {
    companion object {
        private const val TAG = "UpdateService"
    }

    override fun onHandleIntent(intent: Intent?) {
//        val storage=FirebaseStorage.getInstance()
//        val storageReference=storage.reference
//        val apkReference=storageReference.child("apk")
//        val patchReference=storageReference.child("patch")
        val reference = FirebaseDatabase.getInstance().getReference(FirebaseConstant.LATEST_VERSION)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError?) {
                Logs.w(TAG, "onCancelled: " + databaseError?.toException())
                stopSelf()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val version = dataSnapshot.getValue(Version::class.java)
                if (version == null) {
                    stopSelf()
                    return
                }
                if (version.versionCode > getString(R.string.app_version_code).toInt()) {
                    val title = getString(R.string.dialog_update_title, getString(R.string.app_version_name), version.versionName)
                    val text = getString(R.string.dialog_update_text, version.updateLog)
                    val builder = AlertDialog.Builder(APPActivityManager.appManager.currentActivity())
                            .setTitle(title)
                            .setMessage(text)
                            .setPositiveButton("${getString(R.string.action_download_apk)}(${FileUtil.FormatFileSize(version.apkSize)})", { _, _ ->
                                val downloadAPKIntent = Intent(this@UpdateService, DownloadService::class.java)
                                downloadAPKIntent.putExtra("type", "apk")
                                downloadAPKIntent.putExtra("fileName", version.apkDownloadUrl)
                                startService(downloadAPKIntent)
                            })
                    if (version.lastVersionCode == getString(R.string.app_version_code).toInt())
                        builder.setNegativeButton("${getString(R.string.action_download_patch)}(${FileUtil.FormatFileSize(version.patchSize)})", { _, _ ->
                            val downloadPatchIntent = Intent(this@UpdateService, DownloadService::class.java)
                            downloadPatchIntent.putExtra("type", "patch")
                            downloadPatchIntent.putExtra("fileName", version.patchDownloadUrl)
                            startService(downloadPatchIntent)
                        })
                    if (version.must)
                        builder.setOnCancelListener {
                            APPActivityManager.appManager.finishAllActivity()
                        }
                    else
                        builder.setNeutralButton(R.string.action_download_cancel, { _, _ ->
                            Settings.ignoreUpdate = version.versionCode
                        })
                    builder.show()
                }
                stopSelf()
            }
        })

        ScheduleHelper.phpRetrofit
                .create(PhpService::class.java)
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
                        e.printStackTrace()
                    }

                    override fun onComplete() {
                        Logs.i("PhpService", "onComplete: " + update.message)
                        if (update.code == 1 && update.version.versionCode != Settings.ignoreUpdate) {
                            val version = update.version
                            val title = getString(R.string.dialog_update_title, getString(R.string.app_version_name), version.versionName)
                            val text = getString(R.string.dialog_update_text, version.updateLog)
                            val builder = AlertDialog.Builder(APPActivityManager.appManager.currentActivity())
                                    .setTitle(title)
                                    .setMessage(text)
                                    .setPositiveButton("${getString(R.string.action_download_apk)}(${FileUtil.FormatFileSize(version.apkSize)})", { _, _ ->
                                        val downloadAPKIntent = Intent(this@UpdateService, DownloadService::class.java)
                                        downloadAPKIntent.putExtra("type", "apk")
                                        downloadAPKIntent.putExtra("fileName", version.apkDownloadUrl)
                                        startService(downloadAPKIntent)
                                    })
                            if (version.lastVersionCode == getString(R.string.app_version_code).toInt())
                                builder.setNegativeButton("${getString(R.string.action_download_patch)}(${FileUtil.FormatFileSize(version.patchSize)})", { _, _ ->
                                    val downloadPatchIntent = Intent(this@UpdateService, DownloadService::class.java)
                                    downloadPatchIntent.putExtra("type", "patch")
                                    downloadPatchIntent.putExtra("fileName", version.patchDownloadUrl)
                                    startService(downloadPatchIntent)
                                })
                            if (version.must)
                                builder.setOnCancelListener {
                                    APPActivityManager.appManager.finishAllActivity()
                                }
                            else
                                builder.setNeutralButton(R.string.action_download_cancel, { _, _ ->
                                    Settings.ignoreUpdate = version.versionCode
                                })
                            builder.show()
                        }
                        stopSelf()
                    }

                    override fun onNext(update: Update) {
                        this.update = update
                    }
                })
    }
}
