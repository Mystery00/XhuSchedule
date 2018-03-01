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

package com.weilylab.xhuschedule

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.oasisfeng.condom.CondomContext
import com.oasisfeng.condom.CondomOptions
import com.tencent.tauth.Tencent
import com.weilylab.xhuschedule.activity.ErrorActivity
import com.weilylab.xhuschedule.classes.baseClass.XhuScheduleError
import com.weilylab.xhuschedule.listener.EmptyTencentListener
import com.weilylab.xhuschedule.util.Constants
import vip.mystery0.tools.crashHandler.CatchExceptionListener
import vip.mystery0.tools.crashHandler.CrashHandler
import vip.mystery0.tools.logs.Logs
import java.io.File

/**
 * Created by myste.
 */
class APP : MultiDexApplication() {
    companion object {
        private var app: APP? = null
        private var mFirebaseAnalytics: FirebaseAnalytics? = null
        @SuppressLint("StaticFieldLeak")
        private var mFirebaseApp: FirebaseApp? = null
        lateinit var tencent: Tencent
        val tencentListener = EmptyTencentListener()

        fun getContext(): Context = app!!

        fun getFirebaseAnalytics(): FirebaseAnalytics = mFirebaseAnalytics!!
    }

    init {
        app = this
    }

    override fun onCreate() {
        super.onCreate()
        val condom = CondomContext.wrap(applicationContext, "Firebase", CondomOptions().setOutboundJudge { _, _, target_package ->
            target_package == "com.google.android.gms"
        })
        mFirebaseApp = FirebaseApp.initializeApp(condom)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(condom)
        tencent = Tencent.createInstance(Constants.QQ_API_KEY, CondomContext.wrap(applicationContext, "Tencent"))
        Logs.setLevel(Logs.Debug)
        if (!cacheDir.exists())
            cacheDir.mkdirs()
        CrashHandler.getInstance(applicationContext)
                .setDirectory(cacheDir)
                .sendException(object : CatchExceptionListener {
                    override fun onException(date: String, file: File, appVersionName: String, appVersionCode: Int, AndroidVersion: String, sdk: Int, vendor: String, model: String, ex: Throwable) {
                        val error = XhuScheduleError(date, appVersionName, appVersionCode, AndroidVersion, sdk, vendor, model, ex)
                        val bundle = Bundle()
                        bundle.putSerializable("file", file)
                        bundle.putSerializable("error", error)
                        val intent = Intent(applicationContext, ErrorActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("error", bundle)
                        startActivity(intent)
                    }
                })
                .init()
    }
}