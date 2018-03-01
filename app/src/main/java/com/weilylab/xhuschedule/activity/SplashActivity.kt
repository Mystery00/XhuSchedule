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
import com.weilylab.xhuschedule.util.*
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
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
        ScheduleHelper.initChannelID(APP.getContext())//初始化NotificationChannelID
        ScheduleHelper.setTrigger(this)
        ScheduleHelper.checkScreenWidth(this)
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
            val sharedPreference = getSharedPreferences(Constants.SHARED_PREFERENCE_UPDATE_DATA, Context.MODE_PRIVATE)
            Observable.create<Boolean> { subscriber ->
                val colorSharedPreference = getSharedPreferences(Constants.SHARED_PREFERENCE_COURSE_COLOR, MODE_PRIVATE)
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
                var modified = 0L
                cacheDir.listFiles()
                        .filter {
                            it.name.startsWith(Constants.LOG_FILE_PREFIX, true) && it.name.endsWith(Constants.LOG_FILE_SUFFIX, true)
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
                val saveFile = sharedPreference.getString(Constants.SAVE_FILE, "")
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
                            go()
                        }

                        override fun onComplete() {
                        }

                        override fun onNext(t: Boolean) {
                            if (t) {
                                sharedPreference.edit().putString(Constants.SAVE_FILE, latestLog!!.name).apply()
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
                                                    go()
                                                }

                                                override fun done(code: Int, message: String) {
                                                    Toast.makeText(this@SplashActivity, message, Toast.LENGTH_SHORT)
                                                            .show()
                                                    loadingDialog.dismiss()
                                                    go()
                                                }

                                                override fun ready() {
                                                }
                                            })
                                        })
                                        .setNegativeButton(android.R.string.cancel, { _, _ ->
                                            go()
                                        })
                                        .show()
                            } else {
                                go()
                            }
                        }
                    })
        } else
            go()
    }

    private fun go() {
        if (Settings.isFirstEnter)
            startActivity(Intent(this, WelcomeActivity::class.java))
        else
            startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}