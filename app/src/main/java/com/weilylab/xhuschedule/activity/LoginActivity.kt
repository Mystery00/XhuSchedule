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
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.weilylab.xhuschedule.R
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import kotlinx.android.synthetic.main.activity_login.*

import android.app.Activity
import android.support.v4.content.ContextCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.perf.metrics.AddTrace
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.listener.LoginListener
import com.weilylab.xhuschedule.util.XhuFileUtil

class LoginActivity : XhuBaseActivity() {

	private lateinit var loginDialog: ZLoadingDialog

	override fun initView() {
		super.initView()
		loginDialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.STAR_LOADING)
				.setHintText(getString(R.string.hint_dialog_login))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setCancelable(false)
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
	}

	override fun monitor() {
		super.monitor()
		setContentView(R.layout.activity_login)
		login_button.setOnClickListener { attemptLogin() }
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
			val params = Bundle()
			params.putString(FirebaseAnalytics.Param.ITEM_NAME, usernameStr)
			mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, params)
			login()
		}
	}

	@AddTrace(name = "login trace", enabled = true)
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
				val userList = XhuFileUtil.getArrayListFromFile(XhuFileUtil.getStudentListFile(this@LoginActivity), Student::class.java)
				var result = false
				var hasMain = false
				userList.forEach {
					result = result || it.username == student.username
					hasMain = hasMain || it.isMain
				}
				if (!hasMain)
					student.isMain = true
				if (!result)
					userList.add(student)
				XhuFileUtil.saveObjectToFile(userList, XhuFileUtil.getStudentListFile(this@LoginActivity))
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
