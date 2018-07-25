package com.weilylab.xhuschedule.newPackage.ui.activity

import android.app.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.newPackage.base.XhuBaseActivity
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.Test
import com.weilylab.xhuschedule.newPackage.repository.TestRepository
import com.weilylab.xhuschedule.newPackage.ui.adapter.QueryTestRecyclerViewAdapter
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

	private val queryTestListObserver = Observer<List<Test>> {
		queryTestList.clear()
		queryTestList.addAll(it)
		queryTestRecyclerViewAdapter.notifyDataSetChanged()
		hideDialog()
	}

	private val messageObserver = Observer<String> {
		Snackbar.make(coordinatorLayout, it, Snackbar.LENGTH_LONG)
				.show()
	}

	private val requestCodeObserver = Observer<Int> {
		if (it != TestRepository.DONE) {
			hideDialog()
		}
	}

	private val studentObserver = Observer<List<Student>> {
		if (it.isNotEmpty()) {
			TestRepository.queryTests(it[0], queryTestViewModel)
		}
	}

	override fun initView() {
		super.initView()
		setSupportActionBar(toolbar)
		initDialog()
		showDialog()
		recyclerView.layoutManager = LinearLayoutManager(this)
		val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
		dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_query_test)!!)
		recyclerView.addItemDecoration(dividerItemDecoration)
		queryTestRecyclerViewAdapter = QueryTestRecyclerViewAdapter(this, queryTestList)
		recyclerView.adapter = queryTestRecyclerViewAdapter
	}

	private fun initDialog() {
		dialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.STAR_LOADING)
				.setHintText(getString(R.string.hint_dialog_get_tests))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorPrimary))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
				.create()
	}

	override fun initData() {
		super.initData()
		initViewModel()
		TestRepository.queryAllStudent(queryTestViewModel)
	}

	private fun initViewModel() {
		queryTestViewModel = ViewModelProviders.of(this).get(QueryTestViewModel::class.java)
		queryTestViewModel.studentList.observe(this, studentObserver)
		queryTestViewModel.testList.observe(this, queryTestListObserver)
		queryTestViewModel.message.observe(this, messageObserver)
		queryTestViewModel.requestCode.observe(this, requestCodeObserver)
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
