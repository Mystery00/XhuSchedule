/*
 * Created by Mystery0 on 17-12-31 下午8:16.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-31 下午8:16
 */

package com.weilylab.xhuschedule.activity.shortcut

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import com.weilylab.xhuschedule.service.ClearCacheColorService

class RefreshColorActivity:Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(Intent(this, ClearCacheColorService::class.java))
        else
            startService(Intent(this, ClearCacheColorService::class.java))
        Toast.makeText(this,"重新生成课程颜色完成",Toast.LENGTH_SHORT)
                .show()
        finish()
    }
}