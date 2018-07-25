package com.weilylab.xhuschedule.newPackage.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.FragmentTodayBinding
import com.weilylab.xhuschedule.newPackage.ui.adapter.FragmentTodayRecyclerViewAdapter
import com.weilylab.xhuschedule.newPackage.utils.CalendarUtil
import com.weilylab.xhuschedule.newPackage.viewModel.BottomNavigationViewModel
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.tools.base.BaseFragment
import java.util.ArrayList

class TodayFragment : BaseBottomNavigationFragment(R.layout.fragment_today) {

	companion object {
		fun newInstance() = TodayFragment()
	}

	private lateinit var viewModel: BottomNavigationViewModel
	private lateinit var fragmentTodayBinding: FragmentTodayBinding
	private lateinit var adapter: FragmentTodayRecyclerViewAdapter
	private val list = ArrayList<Schedule>()

	private val todayCourseListObserver = Observer<List<Schedule>> {
		list.clear()
		list.addAll(it)
		adapter.notifyDataSetChanged()
		if (list.size == 0)
			fragmentTodayBinding.nullDataView.visibility = View.VISIBLE
		else
			fragmentTodayBinding.nullDataView.visibility = View.GONE
	}

	override fun inflateView(layoutId: Int, inflater: LayoutInflater, container: ViewGroup?): View {
		fragmentTodayBinding = FragmentTodayBinding.inflate(LayoutInflater.from(activity), container, false)
		return fragmentTodayBinding.root
	}

	override fun initView() {
		initViewModel()
		fragmentTodayBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
		adapter = FragmentTodayRecyclerViewAdapter(activity, list)
		fragmentTodayBinding.recyclerView.adapter = adapter
	}

	private fun initViewModel() {
		viewModel = ViewModelProviders.of(activity!!).get(BottomNavigationViewModel::class.java)
		viewModel.todayCourseList.observe(activity!!, todayCourseListObserver)
	}

	override fun monitor() {
		super.monitor()
		adapter.setOnItemClickListener { _, course ->
			viewModel.showCourse.value = arrayListOf(course)
			true
		}
	}

	override fun updateTitle() {
		viewModel.title.value = "第${viewModel.week.value}周 ${CalendarUtil.getWeekIndexInString()}"
	}
}
