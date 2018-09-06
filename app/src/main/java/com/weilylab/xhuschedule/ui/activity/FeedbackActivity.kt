package com.weilylab.xhuschedule.ui.activity

import android.app.Dialog
import android.os.Build
import androidx.core.content.ContextCompat
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.utils.UserUtil
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import vip.mystery0.rxpackagedata.Status

import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.android.synthetic.main.content_feedback.*

class FeedbackActivity : XhuBaseActivity(R.layout.activity_feedback) {
	private lateinit var student: Student
	private lateinit var dialog: Dialog

	override fun initView() {
		super.initView()
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		initDialog()
	}

	override fun initData() {
		super.initData()
		StudentLocalDataSource.queryMainStudent {
			when (it.status) {
				Status.Content -> {
					if (it.data != null)
						student = it.data!!
					else {
						toastMessage(R.string.hint_feedback_null_student)
						finish()
					}
				}
			}
		}
	}

	private fun initDialog() {
		dialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_feedback))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
	}

	override fun monitor() {
		super.monitor()
		toolbar.setNavigationOnClickListener {
			finish()
		}
		buttonSubmit.setOnClickListener {
			val feedbackString = editTextFeedback.text.toString()
			if (feedbackString == "") {
				toastMessage(R.string.hint_feedback_empty)
				return@setOnClickListener
			}
			feedback(feedbackString)
		}
	}

	private fun feedback(message: String) {
		showDialog()
		val appVersion = "${getString(R.string.app_version_name)}-${getString(R.string.app_version_code)}"
		val systemVersion = "Android ${Build.VERSION.RELEASE}-${Build.VERSION.SDK_INT}"
		val manufacturer = Build.MANUFACTURER
		val model = Build.MODEL
		val rom = Build.DISPLAY
		val other = ""
		UserUtil.feedback(student, object : RequestListener<Boolean> {
			override fun done(t: Boolean) {
				hideDialog()
				toastMessage(R.string.hint_feedback_done)
			}

			override fun error(rt: String, msg: String?) {
				hideDialog()
				toastMessage(msg)
			}
		}, appVersion, systemVersion, manufacturer, model, rom, other, message)
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
