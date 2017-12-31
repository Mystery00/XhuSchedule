/*
 * Created by Mystery0 on 17-12-31 下午8:14.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-31 下午8:14
 */

package com.weilylab.xhuschedule.activity.shortcut

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import com.weilylab.xhuschedule.service.WidgetInitService

class RefreshWidgetActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(Intent(this, WidgetInitService::class.java))
        else
            startService(Intent(this, WidgetInitService::class.java))
        Toast.makeText(this, "刷新成功", Toast.LENGTH_SHORT)
                .show()
        finish()
    }
}