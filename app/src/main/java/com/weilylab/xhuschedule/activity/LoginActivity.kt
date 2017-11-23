package com.weilylab.xhuschedule.activity

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.interfaces.RTResponse
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

import kotlinx.android.synthetic.main.content_login.*
import android.animation.ValueAnimator
import android.graphics.Color
import android.util.Base64
import com.google.gson.Gson
import com.weilylab.xhuschedule.classes.ContentRT
import com.weilylab.xhuschedule.util.FileUtil
import java.io.File
import java.io.InputStreamReader
import java.net.UnknownHostException


class LoginActivity : AppCompatActivity() {

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
                .setCancelable(false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            loginDialog.setLoadingColor(resources.getColor(R.color.colorAccent, null))
            loginDialog.setHintTextColor(resources.getColor(R.color.colorAccent, null))
        } else {
            loginDialog.setLoadingColor(Color.parseColor("#4053ff"))
            loginDialog.setHintTextColor(Color.parseColor("#4053ff"))
        }

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

        ScheduleHelper.tomcatRetrofit
                .create(RTResponse::class.java)
                .getCourses(usernameStr, passwordStr)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map({ responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), ContentRT::class.java) })
                .subscribeOn(Schedulers.io())
                .doOnNext { contentRT ->
                    if (contentRT.rt == "1") {
                        val parentFile = File(filesDir.absolutePath + File.separator + "caches/")
                        val base64Name = FileUtil.filterString(Base64.encodeToString(usernameStr.toByteArray(), Base64.DEFAULT))
                        val newFile = File(parentFile, base64Name)
                        newFile.createNewFile()
                        FileUtil.saveObjectToFile(contentRT.courses, newFile)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ContentRT> {

                    private var contentRT: ContentRT? = null

                    override fun onNext(t: ContentRT) {
                        contentRT = t
                    }

                    override fun onError(e: Throwable) {
                        loginDialog.dismiss()
                        e.printStackTrace()
                        if (e is UnknownHostException)
                            Toast.makeText(this@LoginActivity, R.string.error_network, Toast.LENGTH_SHORT)
                                    .show()
                        else
                            Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT)
                                    .show()
                    }

                    override fun onSubscribe(d: Disposable) {
                        loginDialog.show()
                    }

                    override fun onComplete() {
                        loginDialog.dismiss()
                        when (contentRT?.rt) {
                            "0" -> {
                                ScheduleHelper.isLogin = false
                                Toast.makeText(this@LoginActivity, R.string.error_timeout, Toast.LENGTH_SHORT)
                                        .show()
                            }
                            "1" -> {
                                ScheduleHelper.isLogin = true
                                val sharedPreference = getSharedPreferences("cache", Context.MODE_PRIVATE)
                                sharedPreference.edit()
                                        .putString("username", usernameStr)
                                        .putString("password", passwordStr)
                                        .putString("studentName", contentRT?.name)
                                        .apply()
                                Toast.makeText(this@LoginActivity, getString(R.string.success_login, contentRT?.name, getString(R.string.app_name)), Toast.LENGTH_SHORT)
                                        .show()
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                                return
                            }
                            "2" -> {
                                ScheduleHelper.isLogin = false
                                username.error = getString(R.string.error_invalid_username)
                                username.requestFocus()
                            }
                            "3" -> {
                                ScheduleHelper.isLogin = false
                                password.error = getString(R.string.error_invalid_password)
                                password.requestFocus()
                            }
                            else -> {
                                ScheduleHelper.isLogin = false
                                Toast.makeText(this@LoginActivity, R.string.error_other, Toast.LENGTH_SHORT)
                                        .show()
                            }
                        }
                    }
                })
    }
}
