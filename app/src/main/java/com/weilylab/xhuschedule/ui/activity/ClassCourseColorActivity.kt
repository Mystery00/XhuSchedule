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
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.repository.local.CourseLocalDataSource
import com.weilylab.xhuschedule.ui.adapter.ClassCourseColorRecyclerViewAdapter
import com.weilylab.xhuschedule.viewModel.ClassCourseColorViewModel
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import kotlinx.android.synthetic.main.activity_class_course_color.*
import kotlinx.android.synthetic.main.content_class_course_color.*
import vip.mystery0.logs.Logs
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.Status.*

class ClassCourseColorActivity : XhuBaseActivity(R.layout.activity_class_course_color) {
	private val classCourseColorViewModel: ClassCourseColorViewModel by lazy {
		ViewModelProviders.of(this).get(ClassCourseColorViewModel::class.java)
	}
	private val classCourseColorRecyclerViewAdapter: ClassCourseColorRecyclerViewAdapter by lazy { ClassCourseColorRecyclerViewAdapter(this) }
	private val dialog: Dialog by lazy {
		ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(" ")
				.setCanceledOnTouchOutside(false)
				.setDialogBackgroundColor(ContextCompat.getColor(this, R.color.colorWhiteBackground))
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
	}

	private val classCourseColorObserver = Observer<PackageData<List<Course>>> {
		when (it.status) {
			Loading -> showDialog()
			Content -> {
				hideDialog()
				hideNoDataLayout()
				classCourseColorRecyclerViewAdapter.items.clear()
				classCourseColorRecyclerViewAdapter.items.addAll(it.data!!)
				classCourseColorRecyclerViewAdapter.notifyDataSetChanged()
			}
			Empty -> {
				hideDialog()
				showNoDataLayout()
			}
			Error -> {
				Logs.wtfm("classCourseColorObserver: ", it.error)
				hideDialog()
				hideNoDataLayout()
				toastMessage(it.error?.message)
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
		recyclerView.adapter = classCourseColorRecyclerViewAdapter
	}

	override fun initData() {
		super.initData()
		initViewModel()
		CourseLocalDataSource.queryDistinctCourseByUsernameAndTerm(classCourseColorViewModel.classCourseList)
	}

	private fun initViewModel() {
		classCourseColorViewModel.classCourseList.observe(this, classCourseColorObserver)
	}

	override fun monitor() {
		super.monitor()
		toolbar.setNavigationOnClickListener {
			finish()
		}
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
