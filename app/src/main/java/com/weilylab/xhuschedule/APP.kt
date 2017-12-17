/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-21 下午5:35
 */

package com.weilylab.xhuschedule

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.weilylab.xhuschedule.activity.ErrorActivity
import com.weilylab.xhuschedule.classes.XhuScheduleError
import com.weilylab.xhuschedule.service.UpdateService
import com.weilylab.xhuschedule.util.Settings
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
        Logs.setLevel(Logs.LogLevel.Debug)
        val file = File(externalCacheDir.absolutePath + File.separator)
        if (!file.exists())
            file.mkdirs()
        CrashHandler.getInstance(this)
                .setDirectory(file)
                .sendException(object : CatchExceptionListener {
                    override fun onException(date: String, file: File, appVersionName: String, appVersionCode: Int, AndroidVersion: String, sdk: Int, vendor: String, model: String, ex: Throwable) {
                        Logs.i("TAG", "onException: ")
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
        if (Settings.autoCheckUpdate)
            startService(Intent(this, UpdateService::class.java))
    }
}