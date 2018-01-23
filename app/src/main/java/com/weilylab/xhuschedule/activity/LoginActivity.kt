/*
 * Created by Mystery0 on 18-1-12 下午8:52.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                  =====================================================
 *                  =                                                   =
 *                  =                       _oo0oo_                     =
 *                  =                      o8888888o                    =
 *                  =                      88" . "88                    =
 *                  =                      (| -_- |)                    =
 *                  =                      0\  =  /0                    =
 *                  =                    ___/`---'\___                  =
 *                  =                  .' \\|     |# '.                 =
 *                  =                 / \\|||  :  |||# \                =
 *                  =                / _||||| -:- |||||- \              =
 *                  =               |   | \\\  -  #/ |   |              =
 *                  =               | \_|  ''\---/''  |_/ |             =
 *                  =               \  .-\__  '-'  ___/-. /             =
 *                  =             ___'. .'  /--.--\  `. .'___           =
 *                  =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                  =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                  =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                  =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                  =                       `=---='                     =
 *                  =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                  =                                                   =
 *                  =               佛祖保佑         永无BUG              =
 *                  =                                                   =
 *                  =====================================================
 *
 * Last modified 18-1-12 下午8:51
 */

package com.weilylab.xhuschedule.activity

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
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.listener.LoginListener
import com.weilylab.xhuschedule.util.FirebaseUtil
import com.weilylab.xhuschedule.util.XhuFileUtil
import java.io.File

class LoginActivity : BaseActivity() {

    private lateinit var loginDialog: ZLoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        login_button.setOnClickListener { attemptLogin() }
    }

    private fun initView() {
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
//            val params = Bundle()
//            params.putString(FirebaseUtil.STUDENT_NUMBER, usernameStr)
//            mFirebaseAnalytics.logEvent(FirebaseUtil.START_LOGIN, params)
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
        student.login(object : LoginListener {
            override fun error(rt: Int, e: Throwable) {
                loginDialog.dismiss()
                Toast.makeText(this@LoginActivity, e.message.toString(), Toast.LENGTH_SHORT)
                        .show()
            }

            override fun loginDone() {
                ScheduleHelper.isLogin = true
                val userFile = File(filesDir.absolutePath + File.separator + "data" + File.separator + "user")
                val userList = XhuFileUtil.getArrayListFromFile(userFile, Student::class.java)
                var result = false
                userList.forEach {
                    result = result || it.username == student.username
                }
                if (!result)
                    userList.add(student)
                XhuFileUtil.saveObjectToFile(userList, userFile)
                loginDialog.dismiss()
                Toast.makeText(this@LoginActivity, getString(R.string.success_login, getString(R.string.app_name)), Toast.LENGTH_SHORT)
                        .show()
                setResult(Activity.RESULT_OK, intent)
                finish()
                return
            }
        })
    }
}
