/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-17 下午3:36
 */

package com.weilylab.xhuschedule.service

import android.app.IntentService
import android.content.Intent
import android.support.v7.app.AlertDialog
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.Update
import com.weilylab.xhuschedule.interfaces.PhpService
import com.weilylab.xhuschedule.util.APPActivityManager
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.notification.UpdateNotification
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.fileUtil.FileUtil
import vip.mystery0.tools.logs.Logs
import java.io.InputStreamReader

class UpdateService : IntentService("PhpService") {
    override fun onHandleIntent(intent: Intent?) {
        ScheduleHelper.phpRetrofit
                .create(PhpService::class.java)
//                .checkUpdateCall(getString(R.string.app_version_code).toInt())
                .checkUpdateCall(1)
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
                        if (update.code == 1) {
                            val version = update.version
                            UpdateNotification.notify(applicationContext, version)
                            val title = getString(R.string.update_notification_title, getString(R.string.app_version_name), version.versionName)
                            val content = getString(R.string.update_notification_content, FileUtil.FormatFileSize(version.apkSize), FileUtil.FormatFileSize(version.patchSize))
                            val bigText = content + "\n" + getString(R.string.update_notification_big_text, version.updateLog)
                            val builder = AlertDialog.Builder(APPActivityManager.appManager.currentActivity())
                                    .setTitle(title)
                                    .setMessage(bigText)
                                    .setPositiveButton(R.string.action_download_apk, { _, _ ->
                                        val downloadAPKIntent = Intent(this@UpdateService, DownloadService::class.java)
                                        downloadAPKIntent.putExtra("type", "apk")
                                        downloadAPKIntent.putExtra("fileName", version.versionAPK)
                                        startService(downloadAPKIntent)
                                    })
                            if (version.lastVersion == getString(R.string.app_version_code).toInt())
                                builder.setNegativeButton(R.string.action_download_patch, { _, _ ->
                                    val downloadPatchIntent = Intent(this@UpdateService, DownloadService::class.java)
                                    downloadPatchIntent.putExtra("type", "patch")
                                    downloadPatchIntent.putExtra("fileName", version.lastVersionPatch)
                                    startService(downloadPatchIntent)
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
