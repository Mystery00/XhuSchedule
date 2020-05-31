package com.weilylab.xhuschedule.ui.activity

import android.app.Dialog
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.databinding.LayoutNullDataViewBinding
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.ui.adapter.ClassCourseColorRecyclerViewAdapter
import com.weilylab.xhuschedule.viewmodel.ClassCourseColorViewModel
import kotlinx.android.synthetic.main.activity_class_course_color.*
import kotlinx.android.synthetic.main.content_class_course_color.*
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import vip.mystery0.logs.Logs
import vip.mystery0.rx.DataObserver
import vip.mystery0.tools.toastLong

class ClassCourseColorActivity : XhuBaseActivity(R.layout.activity_class_course_color) {
	private val classCourseColorViewModel: ClassCourseColorViewModel by viewModel()
	private val eventBus: EventBus by inject()

	private lateinit var viewStubBinding: LayoutNullDataViewBinding
	private val classCourseColorRecyclerViewAdapter: ClassCourseColorRecyclerViewAdapter by lazy { ClassCourseColorRecyclerViewAdapter(this, classCourseColorViewModel, eventBus) }
	private val dialog: Dialog by lazy { buildDialog(" ") }

	private val classCourseColorObserver = object : DataObserver<List<Course>> {
		override fun loading() {
			showDialog()
		}

		override fun contentNoEmpty(data: List<Course>) {
			hideDialog()
			hideNoDataLayout()
			classCourseColorRecyclerViewAdapter.items.clear()
			classCourseColorRecyclerViewAdapter.items.addAll(data)
		}

		override fun empty() {
			hideDialog()
			showNoDataLayout()
		}

		override fun error(e: Throwable?) {
			Logs.wm(e)
			hideDialog()
			hideNoDataLayout()
			e.toastLong(this@ClassCourseColorActivity)
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
		classCourseColorViewModel.queryDistinctCourseByUsernameAndTerm()
	}

	private fun initViewModel() {
		classCourseColorViewModel.classCourseList.observe(this, classCourseColorObserver)
	}

	override fun monitor() {
		super.monitor()
		toolbar.setNavigationOnClickListener {
			finish()
		}
		nullDataViewStub.setOnInflateListener { _, inflated -> viewStubBinding = DataBindingUtil.bind(inflated)!! }
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
}
