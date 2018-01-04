/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-20 下午11:16
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