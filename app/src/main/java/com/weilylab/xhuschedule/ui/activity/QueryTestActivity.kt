package com.weilylab.xhuschedule.ui.activity

import android.app.Dialog
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.repository.TestRepository
import com.weilylab.xhuschedule.ui.adapter.QueryTestRecyclerViewAdapter
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.Status.*
import com.weilylab.xhuschedule.viewModel.QueryTestViewModel
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE

import kotlinx.android.synthetic.main.activity_query_test.*
import kotlinx.android.synthetic.main.content_query_test.*
import vip.mystery0.logs.Logs

class QueryTestActivity : XhuBaseActivity(R.layout.activity_query_test) {
	private val queryTestViewModel: QueryTestViewModel by lazy {
		ViewModelProviders.of(this)
				.get(QueryTestViewModel::class.java)
	}
	private val dialog: Dialog by lazy {
		ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_get_tests))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setDialogBackgroundColor(ContextCompat.getColor(this, R.color.colorWhiteBackground))
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
	}
	private val queryTestRecyclerViewAdapter: QueryTestRecyclerViewAdapter by lazy { QueryTestRecyclerViewAdapter(this) }

	private val queryTestListObserver = Observer<PackageData<List<Test>>> {
		when (it.status) {
			Loading -> showDialog()
			Content -> {
				hideDialog()
				hideNoDataLayout()
				queryTestRecyclerViewAdapter.items.clear()
				queryTestRecyclerViewAdapter.items.addAll(it.data!!)
				queryTestRecyclerViewAdapter.notifyDataSetChanged()
			}
			Error -> {
				Logs.wtfm("queryTestListObserver: ", it.error)
				hideDialog()
				hideNoDataLayout()
				toastMessage(it.error?.message)
			}
			Empty -> {
				hideDialog()
				showNoDataLayout()
			}
		}
	}

	override fun initView() {
		super.initView()
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		recyclerView.layoutManager = LinearLayoutManager(this)
		val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
		dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_query_test)!!)
		recyclerView.addItemDecoration(dividerItemDecoration)
		recyclerView.adapter = queryTestRecyclerViewAdapter
	}

	override fun initData() {
		super.initData()
		initViewModel()
		TestRepository.queryTests(queryTestViewModel)
	}

	override fun monitor() {
		super.monitor()
		toolbar.setNavigationOnClickListener {
			finish()
		}
	}

	private fun initViewModel() {
		queryTestViewModel.testList.observe(this, queryTestListObserver)
	}

	private fun showDialog() {
		if (!dialog.isShowing)
			dialog.show()
	}

	private fun hideDialog() {
		if (dialog.isShowing)
			dialog.dismiss()
	}

	private fun showNoDataLayout() {
		recyclerView.visibility = View.GONE
		nullDataView.visibility = View.VISIBLE
	}

	private fun hideNoDataLayout() {
		nullDataView.visibility = View.GONE
		recyclerView.visibility = View.VISIBLE
	}
}
