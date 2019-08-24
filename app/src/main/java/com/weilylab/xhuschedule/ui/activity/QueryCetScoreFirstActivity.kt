package com.weilylab.xhuschedule.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.text.TextUtils
import android.view.View
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import com.android.setupwizardlib.view.NavigationBar
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.repository.ScoreRepository
import com.weilylab.xhuschedule.viewmodel.QueryCetScoreViewModelHelper
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import kotlinx.android.synthetic.main.activity_query_cet_score_first.*
import vip.mystery0.logs.Logs
import vip.mystery0.rx.PackageDataObserver
import vip.mystery0.tools.toastLong

class QueryCetScoreFirstActivity : XhuBaseActivity(R.layout.activity_query_cet_score_first) {
	private val dialog: Dialog by lazy {
		ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_get_cet_vcode))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setDialogBackgroundColor(ContextCompat.getColor(this, R.color.colorWhiteBackground))
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
	}

	private val studentInfoListObserver = object : PackageDataObserver<Map<Student, StudentInfo?>> {
		override fun content(data: Map<Student, StudentInfo?>?) {
			val map = data!!
			if (map.keys.isNotEmpty()) {
				val student = map.keys.first { it.isMain }
				QueryCetScoreViewModelHelper.student.value = student
				cetNameEditText.setText(student.studentName)
			}
		}
	}

	private val cetVCodeObserver = object : PackageDataObserver<Bitmap> {
		override fun content(data: Bitmap?) {
			hideDialog()
			startActivity(Intent(this@QueryCetScoreFirstActivity, QueryCetScoreSecondActivity::class.java))
		}

		override fun loading() {
			showDialog()
		}

		override fun empty(data: Bitmap?) {
			hideDialog()
			toastMessage(R.string.hint_data_null, true)
		}

		override fun error(data: Bitmap?, e: Throwable?) {
			Logs.wtfm("cetVCodeObserver: ", e)
			hideDialog()
			e.toastLong(this@QueryCetScoreFirstActivity)
		}
	}

	override fun initView() {
		super.initView()
		setupLayout.setIllustration(ContextCompat.getDrawable(this, R.mipmap.background_cet))
		setupLayout.setIllustrationAspectRatio(2F)
		setupLayout.navigationBar.moreButton.setText(R.string.action_more)
		setupLayout.navigationBar.nextButton.setText(R.string.action_next)
	}

	override fun initData() {
		super.initData()
		initViewModel()
		ScoreRepository.queryAllStudentInfo()
	}

	private fun initViewModel() {
		QueryCetScoreViewModelHelper.studentInfoList.observe(this, studentInfoListObserver)
		QueryCetScoreViewModelHelper.cetVCodeLiveData.observe(this, cetVCodeObserver)
	}

	private fun removeObserver() {
		QueryCetScoreViewModelHelper.studentInfoList.removeObserver(studentInfoListObserver)
		QueryCetScoreViewModelHelper.cetVCodeLiveData.removeObserver(cetVCodeObserver)
	}

	override fun monitor() {
		super.monitor()
		setupLayout.navigationBar.setNavigationBarListener(object : NavigationBar.NavigationBarListener {
			override fun onNavigateBack() {
				finish()
			}

			override fun onNavigateNext() {
				val scrollView = setupLayout.scrollView
				if (scrollView != null) {
					if (scrollView.getChildAt(0).bottom <= scrollView.height + scrollView.scrollY) {
						requestVCode()
					} else {
						scrollView.fullScroll(ScrollView.FOCUS_DOWN)
					}
				}
			}
		})
	}

	override fun onDestroy() {
		super.onDestroy()
		removeObserver()
	}

	private fun requestVCode() {
		if (QueryCetScoreViewModelHelper.studentList.value == null || QueryCetScoreViewModelHelper.studentList.value!!.data == null || QueryCetScoreViewModelHelper.studentList.value!!.data!!.isEmpty()) {
			toastMessage(R.string.hint_action_not_login, true)
			return
		}
		cetNoEditText.error = null
		cetNameEditText.error = null

		val cetNoStr = cetNoEditText.text.toString()
		val cetNameStr = cetNameEditText.text.toString()

		var cancel = false
		var focusView: View? = null

		when {
			TextUtils.isEmpty(cetNoStr) -> {
				cetNoEditText.error = getString(R.string.error_field_required)
				focusView = cetNoEditText
				cancel = true
			}
			TextUtils.isEmpty(cetNameStr) -> {
				cetNameEditText.error = getString(R.string.error_field_required)
				focusView = cetNameEditText
				cancel = true
			}
		}

		if (cancel) {
			focusView?.requestFocus()
			return
		}
		QueryCetScoreViewModelHelper.no.value = cetNoStr
		QueryCetScoreViewModelHelper.name.value = cetNameStr
		ScoreRepository.getCetVCode()
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
