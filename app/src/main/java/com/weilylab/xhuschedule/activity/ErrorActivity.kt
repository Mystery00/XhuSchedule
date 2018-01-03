/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-17 下午6:30
 */

package com.weilylab.xhuschedule.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.XhuScheduleError
import com.weilylab.xhuschedule.listener.UploadLogListener
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import kotlinx.android.synthetic.main.activity_error.*
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class ErrorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)
        if (intent.getBundleExtra("error") == null)
            finish()
        val error = intent.getBundleExtra("error").getSerializable("error") as XhuScheduleError
        val loadingDialog = ZLoadingDialog(this)
                .setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
                .setHintText(getString(R.string.hint_dialog_upload_log))
                .setHintTextSize(16F)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                .create()

        text_date.text = getString(R.string.exception_time, error.time)
        text_version.text = getString(R.string.exception_version, error.appVersionName, error.appVersionCode)
        text_SDK.text = getString(R.string.exception_sdk, error.AndroidVersion, error.sdk)
        text_vendor.text = getString(R.string.exception_vendor, error.vendor)
        text_model.text = getString(R.string.exception_model, error.model)
        val stringWriter = StringWriter()
        error.ex.printStackTrace(PrintWriter(stringWriter))
        text_exception.text = getString(R.string.exception_message, stringWriter.toString())
        button_feedback.setOnClickListener {
            val logFile = intent.getBundleExtra("error").getSerializable("file") as File
            error.uploadLog(this, logFile, object : UploadLogListener {
                override fun error(rt: Int, e: Throwable) {
                    Toast.makeText(this@ErrorActivity, e.message + "\n请将这个信息反馈给开发者", Toast.LENGTH_LONG).show()
                    loadingDialog.dismiss()
                }

                override fun done(code: Int, message: String) {
                    Toast.makeText(this@ErrorActivity, message, Toast.LENGTH_SHORT)
                            .show()
                    loadingDialog.dismiss()
                }

                override fun ready() {
                    loadingDialog.setOnDismissListener {
                        finish()
                    }
                    loadingDialog.show()
                }
            })
        }
    }
}
