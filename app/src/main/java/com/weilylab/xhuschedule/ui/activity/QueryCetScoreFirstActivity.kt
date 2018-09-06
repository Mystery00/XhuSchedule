package com.weilylab.xhuschedule.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.text.TextUtils
import android.view.View
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import kotlinx.android.synthetic.main.activity_query_cet_score_first.*
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.android.setupwizardlib.view.NavigationBar
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.repository.ScoreRepository
import com.weilylab.xhuschedule.viewModel.QueryCetScoreViewModelHelper
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.Status.*
import vip.mystery0.logs.Logs

class QueryCetScoreFirstActivity : XhuBaseActivity(R.layout.activity_query_cet_score_first) {
	private lateinit var dialog: Dialog

	private val studentInfoListObserver = Observer<PackageData<Map<Student, StudentInfo?>>> { data ->
		when (data.status) {
			Content -> {
				val map = data.data!!
				if (map.keys.isNotEmpty()) {
					val student = map.keys.first { it.isMain }
					QueryCetScoreViewModelHelper.student.value = student
					cetNameEditText.setText(student.studentName)
				}
			}
		}
	}

	private val cetVCodeObserver = Observer<PackageData<Bitmap>> {
		when (it.status) {
			Content -> {
				hideDialog()
				startActivity(Intent(this, QueryCetScoreSecondActivity::class.java))
			}
			Loading -> showDialog()
			Empty -> {
				hideDialog()
				toastMessage(R.string.hint_data_null, true)
			}
			Error -> {
				Logs.wtfm("cetVCodeObserver: ", it.error)
				hideDialog()
				toastMessage(it.error?.message, true)
			}
		}
	}

	override fun initView() {
		super.initView()
		initDialog()
		setupLayout.setIllustration(ContextCompat.getDrawable(this, R.mipmap.background_cet))
		setupLayout.navigationBar.moreButton.setText(R.string.action_more)
		setupLayout.navigationBar.nextButton.setText(R.string.action_next)
	}

	private fun initDialog() {
		dialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_get_cet_vcode))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
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
