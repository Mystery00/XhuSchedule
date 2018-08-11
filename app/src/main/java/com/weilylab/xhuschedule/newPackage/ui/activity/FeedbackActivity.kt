package com.weilylab.xhuschedule.newPackage.ui.activity

import android.app.Dialog
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.newPackage.base.XhuBaseActivity
import com.weilylab.xhuschedule.newPackage.config.Status
import com.weilylab.xhuschedule.newPackage.listener.RequestListener
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.newPackage.utils.UserUtil
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE

import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.android.synthetic.main.content_feedback.*

class FeedbackActivity : XhuBaseActivity(R.layout.activity_feedback) {
	private var toast: Toast? = null
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
						student = it.data
					else {
						toast?.cancel()
						toast = Toast.makeText(this, R.string.hint_feedback_null_student, Toast.LENGTH_LONG)
						toast?.show()
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
				toast?.cancel()
				toast = Toast.makeText(this, R.string.hint_feedback_empty, Toast.LENGTH_SHORT)
				toast?.show()
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
				toast?.cancel()
				toast = Toast.makeText(this@FeedbackActivity, R.string.hint_feedback_done, Toast.LENGTH_LONG)
				toast?.show()
			}

			override fun error(rt: String, msg: String?) {
				hideDialog()
				toast?.cancel()
				toast = Toast.makeText(this@FeedbackActivity, msg, Toast.LENGTH_LONG)
				toast?.show()
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
