package com.weilylab.xhuschedule.ui.activity

import android.app.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
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
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.android.synthetic.main.content_feedback.*
import vip.mystery0.logs.Logs
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.Status.*

class FeedbackActivity : XhuBaseActivity(R.layout.activity_feedback) {
	private lateinit var feedBackViewModel: FeedBackViewModel
	private lateinit var dialog: Dialog
	private lateinit var feedBackMessageAdapter: FeedBackMessageAdapter
	private var isRefreshByManual = true
	private var isRefreshPause = false
	private var isRefreshDone = true
	private var isRefreshFinish = true

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
				initAutoRefreshMessage()
				FeedBackRepository.getMessageFromLocal(feedBackViewModel)
			}
			Loading -> showInitDialog()
			Empty -> {
				hideInitDialog()
				toastMessage(R.string.hint_feedback_null_student)
				disableInput()
			}
			Error -> {
				hideInitDialog()
				toastMessage(it.error?.message)
				disableInput()
			}
		}
	}

	private val feedBackMessageObserver = Observer<PackageData<List<FeedBackMessage>>> {
		when (it.status) {
			Content -> {
				hideRefresh()
				addMessage(it.data!!)
				isRefreshByManual = false
				recyclerView.scrollToPosition(feedBackMessageAdapter.items.lastIndex)
			}
			Loading -> if (isRefreshByManual)
				showRefresh()
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
		(recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
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

	private fun initAutoRefreshMessage() {
		if (!isRefreshFinish)
			return
		isRefreshFinish = false
		Observable.create<Boolean> {
			while (!isRefreshFinish) {
				if (!isRefreshPause)
					it.onNext(isRefreshDone)
				Thread.sleep(30 * 1000)
			}
			it.onComplete()
		}
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : io.reactivex.Observer<Boolean> {
					override fun onComplete() {
					}

					override fun onSubscribe(d: Disposable) {
					}

					override fun onNext(t: Boolean) {
						if (t) FeedBackRepository.getMessageFromServer(feedBackViewModel)
					}

					override fun onError(e: Throwable) {
						Logs.wtfm("onError: ", e)
					}
				})
	}

	override fun monitor() {
		super.monitor()
		toolbar.setNavigationOnClickListener {
			finish()
		}
		swipeRefreshLayout.setOnRefreshListener {
			isRefreshByManual = true
			FeedBackRepository.getMessageFromLocal(feedBackViewModel)
		}
		buttonSubmit.setOnClickListener {
			if (inputEditText.text.toString().trim() == "")
				toastMessage(R.string.hint_feedback_empty)
			else {
				FeedBackRepository.sendMessage(inputEditText.text.toString(), feedBackViewModel)
				inputEditText.setText("")
			}
		}
		recyclerView.addOnLayoutChangeListener { _, _, top, _, bottom, _, oldTop, _, oldBottom ->
			if (top != oldTop || bottom != oldBottom)
				recyclerView.scrollToPosition(feedBackMessageAdapter.items.lastIndex)
		}
	}

	override fun onStart() {
		super.onStart()
		isRefreshPause = false
	}

	override fun onStop() {
		super.onStop()
		isRefreshPause = true
	}

	override fun onDestroy() {
		super.onDestroy()
		isRefreshFinish = true
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
		isRefreshByManual = false
	}

	private fun disableInput() {
		buttonSubmit.isEnabled = false
		buttonSubmit.isClickable = false
		buttonSubmit.setOnClickListener(null)
	}

	private fun addMessage(messageList: List<FeedBackMessage>) {
		if (feedBackMessageAdapter.items.isEmpty()) {
			feedBackMessageAdapter.replaceAll(messageList)
			return
		}
		val lastShowMessage = feedBackMessageAdapter.items[feedBackMessageAdapter.items.size - 1]
		val sameMessage = messageList[feedBackMessageAdapter.items.size - 1]
		if (lastShowMessage.id == sameMessage.id && lastShowMessage.createTime == sameMessage.createTime)
			feedBackMessageAdapter.addAll(messageList.subList(feedBackMessageAdapter.items.size, messageList.lastIndex + 1))
		else
			feedBackMessageAdapter.replaceAll(messageList, false)
	}
}
