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
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.Version
import com.weilylab.xhuschedule.interfaces.PhpService
import com.weilylab.xhuschedule.util.APPActivityManager
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.Settings
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.fileUtil.FileUtil
import java.io.InputStreamReader

class UpdateService : IntentService("PhpService") {

    override fun onHandleIntent(intent: Intent?) {
        ScheduleHelper.phpRetrofit
                .create(PhpService::class.java)
                .checkVersion()
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), Version::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<Version>() {
                    private lateinit var version: Version
                    override fun onComplete() {
                        if (version.versionCode > getString(R.string.app_version_code).toInt()) {
                            val title = getString(R.string.dialog_update_title, getString(R.string.app_version_name), version.versionName)
                            val text = getString(R.string.dialog_update_text, version.updateLog)
                            val builder = AlertDialog.Builder(APPActivityManager.appManager.currentActivity())
                                    .setTitle(title)
                                    .setMessage(text)
                                    .setPositiveButton("${getString(R.string.action_download_apk)}(${FileUtil.formatFileSize(version.apkSize)})", { _, _ ->
                                        val downloadAPKIntent = Intent(this@UpdateService, DownloadService::class.java)
                                        downloadAPKIntent.putExtra("type", "apk")
                                        downloadAPKIntent.putExtra("url", version.apkDownloadUrl)
                                        startService(downloadAPKIntent)
                                    })
                            if (version.lastVersionCode == getString(R.string.app_version_code).toInt())
                                builder.setNegativeButton("${getString(R.string.action_download_patch)}(${FileUtil.formatFileSize(version.patchSize)})", { _, _ ->
                                    val downloadPatchIntent = Intent(this@UpdateService, DownloadService::class.java)
                                    downloadPatchIntent.putExtra("type", "patch")
                                    downloadPatchIntent.putExtra("url", version.patchDownloadUrl)
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

                    override fun onNext(version: Version) {
                        this.version = version
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }
}
