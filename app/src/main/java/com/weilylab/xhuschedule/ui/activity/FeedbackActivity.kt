package com.weilylab.xhuschedule.ui.activity

import android.app.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.model.FeedBackMessage
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.FeedBackRepository
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.viewModel.FeedBackViewModel
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE

import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.android.synthetic.main.content_feedback.*
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.Status.*

class FeedbackActivity : XhuBaseActivity(R.layout.activity_feedback) {
	private lateinit var feedBackViewModel: FeedBackViewModel
	private lateinit var dialog: Dialog

	private val mainStudentObserver = Observer<PackageData<Student>> {
		when (it.status) {
			Content -> FeedBackRepository.queryFeedBackToken(feedBackViewModel)
			Loading -> showInitDialog()
			Empty -> {
				hideInitDialog()
				toastMessage(R.string.hint_feedback_null_student)
				finish()
			}
			Error -> {
				hideInitDialog()
				toastMessage(it.error?.message)
			}
		}
	}

	private val feedBackTokenObserver = Observer<PackageData<String>> {
		when (it.status) {
			Content -> FeedBackRepository.getMessageFromLocal(feedBackViewModel)
			Loading -> showInitDialog()
			Empty -> {
				hideInitDialog()
			}
			Error -> {
				hideInitDialog()
			}
		}

	}

	private val feedBackMessageObserver = Observer<PackageData<List<FeedBackMessage>>> {
		when (it.status) {
			Content -> FeedBackRepository.queryFeedBackToken(feedBackViewModel)
			Loading -> showRefresh()
			Empty -> {
				hideRefresh()
			}
			Error -> {
				hideRefresh()
			}
		}

	}

	override fun initView() {
		super.initView()
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		initDialog()
	}

	private fun initDialog() {
		dialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_init))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
	}

	override fun initData() {
		super.initData()
		initViewModel()
		StudentLocalDataSource.queryMainStudent(feedBackViewModel.mainStudent)
	}

	private fun initViewModel() {
		feedBackViewModel = ViewModelProviders.of(this).get(FeedBackViewModel::class.java)
		feedBackViewModel.mainStudent.observe(this, mainStudentObserver)
		feedBackViewModel.feedBackToken.observe(this, feedBackTokenObserver)
		feedBackViewModel.feedBackMessageList.observe(this, feedBackMessageObserver)
	}

	override fun monitor() {
		super.monitor()
		toolbar.setNavigationOnClickListener {
			finish()
		}
		buttonSubmit.setOnClickListener {

		}
	}

	private fun feedback(message: String) {
		showRefresh()
	}

	private fun showInitDialog() {
		if (!dialog.isShowing)
			dialog.show()
	}

	private fun hideInitDialog() {
		if (dialog.isShowing)
			dialog.dismiss()
	}

	private fun showRefresh() {
		if (!swipeRefreshLayout.isRefreshing)
			swipeRefreshLayout.isRefreshing = true
	}

	private fun hideRefresh() {
		if (swipeRefreshLayout.isRefreshing)
			swipeRefreshLayout.isRefreshing = false
	}

	private fun showSendRefresh() {

	}

	private fun hideSendRefresh() {

	}
}
