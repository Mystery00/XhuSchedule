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

package com.weilylab.xhuschedule

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.weilylab.xhuschedule.activity.ErrorActivity
import com.weilylab.xhuschedule.classes.baseClass.XhuScheduleError
import vip.mystery0.tools.crashHandler.CatchExceptionListener
import vip.mystery0.tools.crashHandler.CrashHandler
import vip.mystery0.tools.logs.Logs
import java.io.File

/**
 * Created by myste.
 */
class APP : Application() {
    companion object {
        private var app: APP? = null

        fun getContext(): Context = app!!
    }

    init {
        app = this
    }

    override fun onCreate() {
        super.onCreate()
        Logs.setLevel(Logs.Debug)
        if (!cacheDir.exists())
            cacheDir.mkdirs()
        CrashHandler.getInstance(this)
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