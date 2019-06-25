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

package com.weilylab.xhuschedule.ui.activity

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.LoginRepository
import com.weilylab.xhuschedule.viewmodel.LoginViewModel
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import kotlinx.android.synthetic.main.activity_login.*
import vip.mystery0.logs.Logs
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.Status.*

class LoginActivity : XhuBaseActivity(R.layout.activity_login, false) {
	private val loginViewModel: LoginViewModel by lazy {
		ViewModelProviders.of(this)[LoginViewModel::class.java]
	}
	private val dialog: Dialog by lazy {
		ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_login))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setDialogBackgroundColor(ContextCompat.getColor(this, R.color.colorWhiteBackground))
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
	}

	private val loginObserver = Observer<PackageData<Student>> {
		when (it.status) {
			Content -> {
				if (it.data != null) {
					LoginRepository.queryStudentInfo({ _, e ->
						hideDialog()
						if (e == null) {
							toastMessage(getString(R.string.success_login, getString(R.string.app_name)))
							setResult(Activity.RESULT_OK, intent)
							finish()
						} else
							toastMessage(e.message)
					}, it.data!!)
				}
			}
			Error -> {
				Logs.wtfm("loginObserver: ", it.error)
				hideDialog()
				toastMessage(it.error?.message)
			}
			Loading -> showDialog()
			Empty -> {
			}
		}
	}

	override fun initView() {
		super.initView()
		window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
		window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
		window.statusBarColor = Color.TRANSPARENT
		window.navigationBarColor = Color.TRANSPARENT
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
		LoginRepository.login(student, loginViewModel)
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
