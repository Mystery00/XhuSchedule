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
import com.weilylab.xhuschedule.service.UpdateService
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
        CrashHandler.getInstance(this)
                .setDirectory(cacheDir.absolutePath + File.separator)
                .init()
        startService(Intent(this, UpdateService::class.java))
    }
}