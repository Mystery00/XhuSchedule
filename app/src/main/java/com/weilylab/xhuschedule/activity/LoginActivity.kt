/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-26 下午1:01
 */

package com.weilylab.xhuschedule.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import kotlinx.android.synthetic.main.activity_login.*

import kotlinx.android.synthetic.main.content_login.*
import android.app.Activity
import android.support.v4.content.ContextCompat
import com.weilylab.xhuschedule.classes.Student
import com.weilylab.xhuschedule.listener.LoginListener
import com.weilylab.xhuschedule.util.XhuFileUtil
import vip.mystery0.tools.logs.Logs
import java.io.File

class LoginActivity : AppCompatActivity() {
    companion object {
        private val TAG = "LoginActivity"
    }

    private lateinit var loginDialog: ZLoadingDialog
    private var isAddAccount = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        login_button.setOnClickListener { attemptLogin() }
    }

    private fun initView() {
        isAddAccount = intent.getBooleanExtra("isAddAccount", false)
        loginDialog = ZLoadingDialog(this)
                .setLoadingBuilder(Z_TYPE.STAR_LOADING)
                .setHintText(getString(R.string.hint_dialog_login))
                .setHintTextSize(16F)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
    }

    private fun attemptLogin() {
        username.error = null
        password.error = null

        val usernameStr = username.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        when {
            TextUtils.isEmpty(usernameStr) -> {
                username.error = getString(R.string.error_field_required)
                focusView = username
                cancel = true
            }
            TextUtils.isEmpty(passwordStr) -> {
                password.error = getString(R.string.error_field_required)
                focusView = password
                cancel = true
            }
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            login()
        }
    }

    private fun login() {
        loginDialog.show()
        val usernameStr = username.text.toString()
        val passwordStr = password.text.toString()
        val student = Student()
        student.username = usernameStr
        student.password = passwordStr
        student.login(this, object : LoginListener {
            override fun doInThread() {
                Logs.i(TAG, "doInThread: ")
            }

            override fun error(rt: Int, e: Throwable) {
                loginDialog.dismiss()
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT)
                        .show()
            }

            override fun loginDone(name: String) {
                ScheduleHelper.isLogin = true
                ScheduleHelper.isFromLogin = true
                val userFile = File(filesDir.absolutePath + File.separator + "data" + File.separator + "user")
                student.name = name
                val userList = XhuFileUtil.getArrayListFromFile(userFile, Student::class.java)
                var result = false
                userList.forEach {
                    result = result || it.username == student.username
                }
                if (!result)
                    userList.add(student)
                XhuFileUtil.saveObjectToFile(userList, userFile)
                loginDialog.dismiss()
                Toast.makeText(this@LoginActivity, getString(R.string.success_login, name, getString(R.string.app_name)), Toast.LENGTH_SHORT)
                        .show()
                if (isAddAccount) {
                    setResult(Activity.RESULT_OK, intent)
                } else
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
                return
            }
        })
    }
}
