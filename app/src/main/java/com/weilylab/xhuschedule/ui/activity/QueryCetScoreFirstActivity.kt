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
import kotlinx.android.synthetic.main.activity_query_cet_score_first.*
import vip.mystery0.logs.Logs
import vip.mystery0.rx.DataObserver

class QueryCetScoreFirstActivity : XhuBaseActivity(R.layout.activity_query_cet_score_first) {
	private val dialog: Dialog by lazy { buildDialog(R.string.hint_dialog_get_cet_vcode) }

	private val studentInfoListObserver = object : DataObserver<Map<Student, StudentInfo?>> {
		override fun contentNoEmpty(data: Map<Student, StudentInfo?>) {
			if (data.keys.isNotEmpty()) {
				val student = data.keys.first { it.isMain }
				QueryCetScoreViewModelHelper.student.value = student
				cetNameEditText.setText(student.studentName)
			}
		}
	}

	private val cetVCodeObserver = object : DataObserver<Bitmap> {
		override fun contentNoEmpty(data: Bitmap) {
			hideDialog()
			startActivity(Intent(this@QueryCetScoreFirstActivity, QueryCetScoreSecondActivity::class.java))
		}

		override fun loading() {
			showDialog()
		}

		override fun empty() {
			hideDialog()
			toastLong(R.string.hint_data_null)
		}

		override fun error(e: Throwable?) {
			Logs.wtfm("cetVCodeObserver: ", e)
			hideDialog()
			toastLong(e)
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
			toastLong(R.string.hint_action_not_login)
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
