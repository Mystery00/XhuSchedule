/*
 * Created by Mystery0 on 17-12-11 下午9:08.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-11 下午9:08
 */

package com.weilylab.xhuschedule.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.weilylab.xhuschedule.util.Settings
import java.io.File

/**
 * Created by mystery0.
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Settings.autoCheckLog) {
            val dir = File(externalCacheDir.absolutePath + File.separator)

        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}