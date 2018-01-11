/*
 * Created by Mystery0 on 18-1-11 下午4:25.
 * Copyright (c) 2018. All Rights reserved.
 *
 * Last modified 18-1-11 下午4:25
 */

package com.weilylab.xhuschedule.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.weilylab.xhuschedule.util.APPActivityManager

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        APPActivityManager.appManager.addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        APPActivityManager.appManager.finishActivity(this)
    }
}