/*
 * Created by Mystery0 on 18-2-21 下午9:12.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 18-2-21 下午9:11
 */

package com.weilylab.xhuschedule.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.XhuScheduleError
import com.weilylab.xhuschedule.listener.UploadLogListener
import com.weilylab.xhuschedule.util.Constants
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import kotlinx.android.synthetic.main.activity_error.*
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class ErrorActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)
        if (intent.getBundleExtra(Constants.INTENT_TAG_NAME_ERROR) == null)
            finish()
        val error = intent.getBundleExtra(Constants.INTENT_TAG_NAME_ERROR).getSerializable(Constants.INTENT_TAG_NAME_ERROR) as XhuScheduleError
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
            val logFile = intent.getBundleExtra(Constants.INTENT_TAG_NAME_ERROR).getSerializable(Constants.INTENT_TAG_NAME_FILE) as File
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
