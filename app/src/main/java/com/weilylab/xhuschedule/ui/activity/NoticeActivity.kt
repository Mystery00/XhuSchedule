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

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.ui.adapter.NoticeAdapter
import com.weilylab.xhuschedule.model.Notice
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.databinding.LayoutNullDataViewBinding
import com.weilylab.xhuschedule.repository.NoticeRepository
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import com.weilylab.xhuschedule.viewmodel.NoticeViewModel
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.Status.*
import kotlinx.android.synthetic.main.activity_notice.*
import vip.mystery0.logs.Logs

class NoticeActivity : XhuBaseActivity(R.layout.activity_notice) {
	private val noticeViewModel: NoticeViewModel by lazy {
		ViewModelProviders.of(this).get(NoticeViewModel::class.java)
	}
	private val noticeAdapter: NoticeAdapter by lazy { NoticeAdapter(this) }
	private lateinit var viewStubBinding: LayoutNullDataViewBinding

	private val noticeObserver = Observer<PackageData<List<Notice>>> {
		when (it.status) {
			Loading -> showRefresh()
			Content -> {
				hideRefresh()
				hideNoDataLayout()
				noticeAdapter.items.clear()
				noticeAdapter.items.addAll(it.data!!)
				noticeAdapter.notifyDataSetChanged()
			}
			Error -> {
				Logs.wtfm("noticeObserver: ", it.error)
				hideRefresh()
				hideNoDataLayout()
				toastMessage(it.error?.message)
			}
			Empty -> {
				hideRefresh()
				showNoDataLayout()
			}
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
			LayoutRefreshConfigUtil.isRefreshNoticeDot = true
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
		NoticeRepository.queryNoticeForAndroid(noticeViewModel)
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
		LayoutRefreshConfigUtil.isRefreshNoticeDot = true
		NoticeRepository.markNoticesAsRead(noticeAdapter.items)
		super.onDestroy()
	}
}
