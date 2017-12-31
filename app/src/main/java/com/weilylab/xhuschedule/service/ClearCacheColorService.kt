/*
 * Created by Mystery0 on 17-12-31 下午8:18.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-31 下午8:18
 */

package com.weilylab.xhuschedule.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ClearCacheColorService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val colorSharedPreference = getSharedPreferences("course_color", MODE_PRIVATE)
        colorSharedPreference.all.keys.forEach {
            colorSharedPreference.edit().remove(it).apply()
        }
        stopSelf()
    }
}
