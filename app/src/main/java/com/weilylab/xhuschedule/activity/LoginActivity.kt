/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-26 下午1:01
 */

package com.weilylab.xhuschedule.activity

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
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
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_login.*

import kotlinx.android.synthetic.main.content_login.*
import android.animation.ValueAnimator
import android.app.Activity
import android.support.v4.content.ContextCompat
import com.weilylab.xhuschedule.classes.LoginRT
import com.weilylab.xhuschedule.classes.Student
import com.weilylab.xhuschedule.util.XhuFileUtil
import java.io.File
import java.net.UnknownHostException

class LoginActivity : AppCompatActivity() {

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

        val colorAnim = ObjectAnimator.ofInt(login_form, "backgroundColor", -0x7f80, -0x7f7f01)
        colorAnim.duration = 3000
        colorAnim.setEvaluator(ArgbEvaluator())
        colorAnim.repeatCount = ValueAnimator.INFINITE
        colorAnim.repeatMode = ValueAnimator.REVERSE
        colorAnim.start()
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
        val usernameStr = username.text.toString()
        val passwordStr = password.text.toString()
        val student = Student()
        student.username = usernameStr
        student.password = passwordStr
        student.login()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<LoginRT> {
                    private var loginRT: LoginRT? = null
                    override fun onNext(t: LoginRT) {
                        loginRT = t
                    }

                    override fun onError(e: Throwable) {
                        loginDialog.dismiss()
                        e.printStackTrace()
                        if (e is UnknownHostException)
                            Toast.makeText(this@LoginActivity, R.string.error_network, Toast.LENGTH_SHORT)
                                    .show()
                        else
                            Toast.makeText(this@LoginActivity, e.message + "，请重试", Toast.LENGTH_SHORT)
                                    .show()
                    }

                    override fun onSubscribe(d: Disposable) {
                        loginDialog.show()
                    }

                    override fun onComplete() {
                        loginDialog.dismiss()
                        when (loginRT?.rt) {
                            "0" -> {
                                ScheduleHelper.isLogin = isAddAccount
                                Toast.makeText(this@LoginActivity, R.string.error_timeout, Toast.LENGTH_SHORT)
                                        .show()
                            }
                            "1" -> {
                                ScheduleHelper.isLogin = true
                                ScheduleHelper.isFromLogin = true
                                val userFile = File(filesDir.absolutePath + File.separator + "data" + File.separator + "user")
                                student.name = loginRT?.name!!
                                val userList = XhuFileUtil.getStudentsFromFile(userFile)
                                if (!userList.contains(student))
                                    userList.add(student)
                                XhuFileUtil.saveObjectToFile(userList, userFile)
                                Toast.makeText(this@LoginActivity, getString(R.string.success_login, loginRT?.name, getString(R.string.app_name)), Toast.LENGTH_SHORT)
                                        .show()
                                if (isAddAccount) {
                                    setResult(Activity.RESULT_OK, intent)
                                } else
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                                return
                            }
                            "2" -> {
                                ScheduleHelper.isLogin = isAddAccount
                                username.error = getString(R.string.error_invalid_username)
                                username.requestFocus()
                            }
                            "3" -> {
                                ScheduleHelper.isLogin = isAddAccount
                                password.error = getString(R.string.error_invalid_password)
                                password.requestFocus()
                            }
                            else -> {
                                ScheduleHelper.isLogin = isAddAccount
                                Toast.makeText(this@LoginActivity, R.string.error_other, Toast.LENGTH_SHORT)
                                        .show()
                            }
                        }
                    }
                })
    }
}
