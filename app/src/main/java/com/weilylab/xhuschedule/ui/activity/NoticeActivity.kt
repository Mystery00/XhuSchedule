/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.activity

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.databinding.LayoutNullDataViewBinding
import com.weilylab.xhuschedule.model.Notice
import com.weilylab.xhuschedule.model.event.UI
import com.weilylab.xhuschedule.model.event.UIConfigEvent
import com.weilylab.xhuschedule.ui.adapter.NoticeAdapter
import com.weilylab.xhuschedule.viewmodel.NoticeViewModel
import kotlinx.android.synthetic.main.activity_notice.*
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import vip.mystery0.logs.Logs
import vip.mystery0.rx.DataObserver

class NoticeActivity : XhuBaseActivity(R.layout.activity_notice) {
	private val noticeViewModel: NoticeViewModel by viewModel()
	private val eventBus: EventBus by inject()
	private val noticeAdapter: NoticeAdapter by lazy { NoticeAdapter(this) }
	private lateinit var viewStubBinding: LayoutNullDataViewBinding

	private val noticeObserver = object : DataObserver<List<Notice>> {
		override fun loading() {
			showRefresh()
		}

		override fun contentNoEmpty(data: List<Notice>) {
			hideRefresh()
			hideNoDataLayout()
			noticeAdapter.items.clear()
			noticeAdapter.items.addAll(data)
		}

		override fun error(e: Throwable?) {
			Logs.wm(e)
			hideRefresh()
			hideNoDataLayout()
			toastLong(e)
		}

		override fun empty() {
			hideRefresh()
			showNoDataLayout()
		}
	}

	override fun initView() {
		super.initView()
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		recyclerView.layoutManager = LinearLayoutManager(this)
		recyclerView.adapter = noticeAdapter
		recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
		swipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_light,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light)
		showRefresh()
	}

	override fun initData() {
		super.initData()
		initViewModel()
	}

	override fun requestData() {
		super.requestData()
		refresh()
	}

	override fun monitor() {
		super.monitor()
		toolbar.setNavigationOnClickListener {
			eventBus.post(UIConfigEvent(arrayListOf(UI.NOTICE_DOT)))
			finish()
		}
		swipeRefreshLayout.setOnRefreshListener {
			refresh()
		}
		nullDataViewStub.setOnInflateListener { _, inflated -> viewStubBinding = DataBindingUtil.bind(inflated)!! }
	}

	private fun initViewModel() {
		noticeViewModel.noticeList.observe(this, noticeObserver)
	}

	private fun refresh() {
		noticeViewModel.queryNotice()
	}

	private fun showRefresh() {
		if (!swipeRefreshLayout.isRefreshing)
			swipeRefreshLayout.isRefreshing = true
	}

	private fun hideRefresh() {
		if (swipeRefreshLayout.isRefreshing)
			swipeRefreshLayout.isRefreshing = false
	}

	private fun showNoDataLayout() {
		try {
			nullDataViewStub.inflate()
		} catch (e: Exception) {
			viewStubBinding.root.visibility = View.VISIBLE
		}
		recyclerView.visibility = View.GONE
	}

	private fun hideNoDataLayout() {
		if (::viewStubBinding.isInitialized)
			viewStubBinding.root.visibility = View.GONE
		recyclerView.visibility = View.VISIBLE
	}

	override fun onDestroy() {
		noticeViewModel.markListAsRead(noticeAdapter.items)
		eventBus.post(UIConfigEvent(arrayListOf(UI.NOTICE_DOT)))
		super.onDestroy()
	}
}
