/*
 * Created by Mystery0 on 17-11-27 下午9:52.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-27 下午9:52
 */

package com.weilylab.xhuschedule.activity

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Error
import kotlinx.android.synthetic.main.activity_error.*
import java.io.PrintWriter
import java.io.StringWriter

class ErrorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)
        if (intent.getBundleExtra("error") == null)
            finish()
        val error = intent.getBundleExtra("error").getSerializable("error") as Error

        text_date.text = getString(R.string.exception_time, error.time)
        text_version.text = getString(R.string.exception_version, error.appVersionName, error.appVersionCode)
        text_SDK.text = getString(R.string.exception_sdk, error.AndroidVersion, error.sdk)
        text_vendor.text = getString(R.string.exception_vendor, error.vendor)
        text_model.text = getString(R.string.exception_model, error.model)
        val stringWriter = StringWriter()
        error.ex.printStackTrace(PrintWriter(stringWriter))
        text_exception.text = getString(R.string.exception_message, stringWriter.toString())
        button_feedback.setOnClickListener {
            val stringBuilder = StringBuilder()
            stringBuilder.appendln("App Version: " + error.appVersionName + "-" + error.appVersionCode)
            stringBuilder.appendln("OS Version: " + error.AndroidVersion + "-" + error.sdk)
            stringBuilder.appendln("Vendor: " + error.vendor)
            stringBuilder.appendln("Model: " + error.model)
            stringBuilder.appendln("Detail: " + stringWriter.toString())
            stringBuilder.appendln("请描述导致闪退的操作：")
            val data = Intent(Intent.ACTION_SENDTO)
            data.data = Uri.parse("mailto:mystery0dyl520@gmail.com")
            data.putExtra(Intent.EXTRA_SUBJECT, "西瓜课表反馈")
            data.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString())
            startActivity(data)
        }
    }
}
