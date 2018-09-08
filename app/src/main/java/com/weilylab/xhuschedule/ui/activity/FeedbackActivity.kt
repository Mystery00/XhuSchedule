package com.weilylab.xhuschedule.ui.activity

import android.app.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.model.FeedBackMessage
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.FeedBackRepository
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.ui.adapter.FeedBackMessageAdapter
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
	private lateinit var feedBackMessageAdapter: FeedBackMessageAdapter

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
			Content -> {
				hideInitDialog()
				FeedBackRepository.getMessageFromServer(feedBackViewModel, 0)
			}
			Loading -> showInitDialog()
			Empty -> {
				hideInitDialog()
				toastMessage(R.string.hint_feedback_null_student)
			}
			Error -> {
				hideInitDialog()
				toastMessage(it.error?.message)
			}
		}
	}

	private val feedBackMessageObserver = Observer<PackageData<List<FeedBackMessage>>> {
		when (it.status) {
			Content -> {
				hideRefresh()
				addMessage(it.data!!)
				recyclerView.scrollToPosition(feedBackMessageAdapter.items.lastIndex)
			}
			Loading -> showRefresh()
			Empty -> {
				hideRefresh()
			}
			Error -> {
				hideRefresh()
				toastMessage(it.error?.message)
			}
		}
	}

	override fun initView() {
		super.initView()
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		initDialog()
		recyclerView.layoutManager = LinearLayoutManager(this)
		feedBackMessageAdapter = FeedBackMessageAdapter()
		recyclerView.adapter = feedBackMessageAdapter
		recyclerView.itemAnimator = DefaultItemAnimator()
		swipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_light,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light)
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
		swipeRefreshLayout.setOnRefreshListener {
			FeedBackRepository.getMessageFromServer(feedBackViewModel, 0)
		}
		buttonSubmit.setOnClickListener {
			if (inputEditText.text.toString().trim() == "")
				toastMessage(R.string.hint_feedback_empty)
			else {
				FeedBackRepository.sendMessage(inputEditText.text.toString(), feedBackViewModel)
				inputEditText.setText("")
			}
		}
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

	private fun addMessage(messageList: List<FeedBackMessage>) {
		if (feedBackMessageAdapter.items.isEmpty()) {
			feedBackMessageAdapter.replaceAll(messageList, false)
			return
		}
		val lastMessageIndex = feedBackMessageAdapter.items.lastIndex
		val lastShowMessage = feedBackMessageAdapter.items.last()
		val sameMessage = messageList[lastMessageIndex]
		if (lastShowMessage.id == sameMessage.id && lastShowMessage.createTime == sameMessage.createTime)
			feedBackMessageAdapter.addAll(messageList.subList(lastMessageIndex + 1, messageList.lastIndex + 1))
		else
			feedBackMessageAdapter.replaceAll(messageList, false)
	}
}
