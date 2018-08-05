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

package com.weilylab.xhuschedule.newPackage.ui.activity

import android.app.Dialog
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.newPackage.ui.adapter.NoticeAdapter
import com.weilylab.xhuschedule.newPackage.model.Notice
import com.weilylab.xhuschedule.newPackage.base.XhuBaseActivity
import com.weilylab.xhuschedule.newPackage.config.Status.*
import com.weilylab.xhuschedule.newPackage.repository.NoticeRepository
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.newPackage.viewModel.NoticeViewModel
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import kotlinx.android.synthetic.main.activity_notice.*

class NoticeActivity : XhuBaseActivity(R.layout.activity_notice) {
	private lateinit var noticeViewModel: NoticeViewModel
	private lateinit var dialog: Dialog
	private lateinit var noticeAdapter: NoticeAdapter
	private val noticeList = ArrayList<Notice>()

	private val noticeObserver = Observer<PackageData<List<Notice>>> {
		when (it.status) {
			Loading -> showDialog()
			Content -> {
				hideDialog()
				noticeList.clear()
				noticeList.addAll(it.data!!)
				noticeAdapter.notifyDataSetChanged()
			}
			Error -> {
				hideDialog()
				Toast.makeText(this, it.error?.message, Toast.LENGTH_LONG)
						.show()
			}
			Empty -> {
				hideDialog()
			}
		}
	}

	override fun initView() {
		super.initView()
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		initDialog()
		recyclerView.layoutManager = LinearLayoutManager(this)
		noticeAdapter = NoticeAdapter(this, noticeList)
		recyclerView.adapter = noticeAdapter
		recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
		swipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_light,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light)
		swipeRefreshLayout.isRefreshing = true
	}

	private fun initDialog() {
		dialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_get_notices))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
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
			finish()
		}
		swipeRefreshLayout.setOnRefreshListener {
			refresh()
		}
	}

	private fun initViewModel() {
		noticeViewModel = ViewModelProviders.of(this).get(NoticeViewModel::class.java)
		noticeViewModel.noticeList.observe(this, noticeObserver)
	}

	private fun refresh() {
		NoticeRepository.queryNoticeForAndroid(noticeViewModel)
	}

	private fun showDialog() {
		if (!dialog.isShowing)
			dialog.show()
	}

	private fun hideDialog() {
		if (dialog.isShowing)
			dialog.dismiss()
	}

	override fun onDestroy() {
		NoticeRepository.markNoticesAsRead(noticeList)
		super.onDestroy()
	}
}
