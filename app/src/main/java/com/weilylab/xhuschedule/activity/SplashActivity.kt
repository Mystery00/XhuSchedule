/*
 * Created by Mystery0 on 18-2-21 下午9:12.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 18-2-21 下午9:11
 */

package com.weilylab.xhuschedule.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.weilylab.xhuschedule.APP
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.XhuScheduleError
import com.weilylab.xhuschedule.listener.UploadLogListener
import com.weilylab.xhuschedule.service.UpdateService
import com.weilylab.xhuschedule.util.CalendarUtil
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.XhuFileUtil
import com.weilylab.xhuschedule.util.notification.TomorrowInfoNotification
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.logs.Logs
import java.io.File
import java.util.*
import kotlin.math.max

/**
 * Created by mystery0.
 */
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.START_DATE, Calendar.getInstance().time.toString())
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, params)
        Logs.i("tag", "onCreate: initChannelID")
        ScheduleHelper.initChannelID(APP.getContext())//初始化NotificationChannelID
        TomorrowInfoNotification.notify(this)
        if (Settings.autoCheckUpdate)
            startService(Intent(this, UpdateService::class.java))
        if (Settings.autoCheckLog) {
            val loadingDialog = ZLoadingDialog(this)
                    .setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
                    .setHintText(getString(R.string.hint_dialog_upload_log))
                    .setHintTextSize(16F)
                    .setCanceledOnTouchOutside(false)
                    .setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
                    .setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                    .create()
            var latestLog: File? = null
            var error: XhuScheduleError? = null
            val sharedPreference = getSharedPreferences("updateData", Context.MODE_PRIVATE)
            Observable.create<Boolean> { subscriber ->
                val colorSharedPreference = getSharedPreferences("course_color", MODE_PRIVATE)
                /**
                 * =============================================
                 * 为了兼容旧版本，在这里将旧版本的数据做一次清理
                 */
                val isNeedClear = colorSharedPreference.all.keys.any { it.contains("_trans") }
                if (isNeedClear)
                    colorSharedPreference.all.keys.forEach {
                        colorSharedPreference.edit().remove(it).apply()
                    }
                /**
                 * ==============================================
                 */
                //log文件前缀名
                val fileNamePrefix = "crash"
                //log文件的扩展名
                val fileNameSuffix = "txt"
                var modified = 0L
                cacheDir.listFiles()
                        .filter {
                            it.name.startsWith(fileNamePrefix, true) && it.name.endsWith(fileNameSuffix, true)
                        }
                        .forEach {
                            if (modified < it.lastModified()) {
                                modified = max(it.lastModified(), modified)
                                latestLog = it
                            }
                        }
                if (modified == 0L) {
                    subscriber.onNext(false)
                    subscriber.onComplete()
                    return@create
                }
                val saveFile = sharedPreference.getString("saveFile", "")
                error = XhuFileUtil.parseLog(latestLog!!)
                subscriber.onNext(saveFile != latestLog!!.name)
                subscriber.onComplete()
            }
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : DisposableObserver<Boolean>() {
                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                            finish()
                        }

                        override fun onComplete() {
                        }

                        override fun onNext(t: Boolean) {
                            if (t) {
                                sharedPreference.edit().putString("saveFile", latestLog!!.name).apply()
                                AlertDialog.Builder(this@SplashActivity)
                                        .setTitle(" ")
                                        .setMessage(getString(R.string.hint_check_log, latestLog!!.name, latestLog!!.absolutePath, CalendarUtil.showDate(latestLog!!.lastModified())))
                                        .setPositiveButton(R.string.action_feedback, { _, _ ->
                                            loadingDialog.show()
                                            error!!.uploadLog(this@SplashActivity, latestLog!!, object : UploadLogListener {
                                                override fun error(rt: Int, e: Throwable) {
                                                    e.printStackTrace()
                                                    Toast.makeText(this@SplashActivity, e.message + "\n请将这个信息反馈给开发者", Toast.LENGTH_LONG).show()
                                                    loadingDialog.dismiss()
                                                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                                    finish()
                                                }

                                                override fun done(code: Int, message: String) {
                                                    Toast.makeText(this@SplashActivity, message, Toast.LENGTH_SHORT)
                                                            .show()
                                                    loadingDialog.dismiss()
                                                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                                    finish()
                                                }

                                                override fun ready() {
                                                }
                                            })
                                        })
                                        .setNegativeButton(android.R.string.cancel, { _, _ ->
                                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                            finish()
                                        })
                                        .show()
                            } else {
                                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                finish()
                            }
                        }
                    })
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}