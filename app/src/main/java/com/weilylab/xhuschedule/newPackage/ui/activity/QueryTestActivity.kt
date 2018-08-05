package com.weilylab.xhuschedule.newPackage.ui.activity

import android.app.Dialog
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.newPackage.base.XhuBaseActivity
import com.weilylab.xhuschedule.newPackage.config.Status
import com.weilylab.xhuschedule.newPackage.config.Status.*
import com.weilylab.xhuschedule.newPackage.model.Test
import com.weilylab.xhuschedule.newPackage.repository.TestRepository
import com.weilylab.xhuschedule.newPackage.ui.adapter.QueryTestRecyclerViewAdapter
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.newPackage.viewModel.QueryTestViewModel
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE

import kotlinx.android.synthetic.main.activity_query_test.*
import kotlinx.android.synthetic.main.content_query_test.*
import java.util.ArrayList

class QueryTestActivity : XhuBaseActivity(R.layout.activity_query_test) {
	private lateinit var queryTestViewModel: QueryTestViewModel
	private lateinit var dialog: Dialog
	private lateinit var queryTestRecyclerViewAdapter: QueryTestRecyclerViewAdapter
	private val queryTestList = ArrayList<Test>()

	private val queryTestListObserver = Observer<PackageData<List<Test>>> {
		when (it.status) {
			Loading -> showDialog()
			Content -> {
				hideDialog()
				queryTestList.clear()
				queryTestList.addAll(it.data!!)
				queryTestRecyclerViewAdapter.notifyDataSetChanged()
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
		val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
		dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_query_test)!!)
		recyclerView.addItemDecoration(dividerItemDecoration)
		queryTestRecyclerViewAdapter = QueryTestRecyclerViewAdapter(this, queryTestList)
		recyclerView.adapter = queryTestRecyclerViewAdapter
	}

	private fun initDialog() {
		dialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_get_tests))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
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
		queryTestViewModel = ViewModelProviders.of(this).get(QueryTestViewModel::class.java)
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
}
