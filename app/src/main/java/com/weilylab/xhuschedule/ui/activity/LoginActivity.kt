/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.activity

import android.app.Activity
import android.app.Dialog
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import vip.mystery0.logs.Logs
import vip.mystery0.rx.DataObserver

class LoginActivity : XhuBaseActivity(R.layout.activity_login, false) {
	private val loginViewModel: LoginViewModel by viewModel()
	private val dialog: Dialog by lazy { buildDialog(R.string.hint_dialog_login) }

	private val loginObserver = object : DataObserver<Student> {
		override fun contentNoEmpty(data: Student) {
			hideDialog()
			toast(getString(R.string.success_login, getString(R.string.app_name)))
			setResult(Activity.RESULT_OK, intent)
			finish()
		}

		override fun error(e: Throwable?) {
			Logs.w(e)
			hideDialog()
			toastLong(e)
		}

		override fun loading() {
			showDialog()
		}
	}

	override fun inflateView(layoutId: Int) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
			window.attributes = window.attributes.apply {
				layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
			}
		}
		super.inflateView(layoutId)
	}

	override fun initData() {
		super.initData()
		initViewModel()
	}

	private fun initViewModel() {
		loginViewModel.loginLiveData.observe(this, loginObserver)
	}

	override fun monitor() {
		super.monitor()
		login_button.setOnClickListener { attemptLogin() }
	}

	private fun attemptLogin() {
		username_edit_text.error = null
		password_edit_text.error = null

		val usernameStr = username_edit_text.text.toString()
		val passwordStr = password_edit_text.text.toString()

		var cancel = false
		var focusView: View? = null

		when {
			TextUtils.isEmpty(usernameStr) -> {
				username_edit_text.error = getString(R.string.error_field_required)
				focusView = username_edit_text
				cancel = true
			}
			TextUtils.isEmpty(passwordStr) -> {
				password_edit_text.error = getString(R.string.error_field_required)
				focusView = password_edit_text
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
		showDialog()
		val usernameStr = username_edit_text.text.toString()
		val passwordStr = password_edit_text.text.toString()
		val student = Student()
		student.username = usernameStr
		student.password = passwordStr
		loginViewModel.login(student)
	}

	private fun showDialog() {
		if (!dialog.isShowing)
			dialog.show()
	}

	private fun hideDialog() {
		if (dialog.isShowing)
			dialog.dismiss()
	}
}
