package com.weilylab.xhuschedule.newPackage.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.FragmentTableBinding
import com.weilylab.xhuschedule.newPackage.model.Course
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.repository.CourseRepository
import com.weilylab.xhuschedule.newPackage.viewModel.BottomNavigationViewModel
import vip.mystery0.tools.base.BaseFragment
import java.text.SimpleDateFormat
import java.util.*

class TableFragment : BaseFragment(R.layout.fragment_table) {
	private lateinit var fragmentTableBinding: FragmentTableBinding
	private lateinit var bottomNavigationViewModel: BottomNavigationViewModel

	private val courseListObserver = Observer<List<Course>> {
		fragmentTableBinding.timeTableView.setSource(it).updateView()
	}

	private val studentObserver = Observer<List<Student>> {
		if (it.isNotEmpty()) {
			CourseRepository.getCourseCacheByStudent(it[0], bottomNavigationViewModel)
		}
	}

	private val weekObserver = Observer<Int> {
		fragmentTableBinding.timeTableView.changeWeekOnly(it)
	}

	private val startDateTimeObserver = Observer<Calendar> {
		val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
		fragmentTableBinding.timeTableView.setCurWeek(simpleDateFormat.format(it.time))
	}

	companion object {
		fun newInstance() = TableFragment()
	}

	override fun inflateView(layoutId: Int, inflater: LayoutInflater, container: ViewGroup?): View {
		fragmentTableBinding = FragmentTableBinding.inflate(inflater, container, false)
		return fragmentTableBinding.root
	}

	override fun initView() {
		initViewModel()
		fragmentTableBinding.timeTableView.initView(activity!!)
	}

	private fun initViewModel() {
		bottomNavigationViewModel = ViewModelProviders.of(activity!!).get(BottomNavigationViewModel::class.java)
		bottomNavigationViewModel.courseList.observe(activity!!, courseListObserver)
		bottomNavigationViewModel.studentList.observe(activity!!, studentObserver)
		bottomNavigationViewModel.week.observe(activity!!, weekObserver)
		bottomNavigationViewModel.startDateTime.observe(activity!!, startDateTimeObserver)
	}

	override fun monitor() {
		super.monitor()
		fragmentTableBinding.timeTableView.scheduleManager
				.setOnItemClickListener { _, scheduleList ->
					scheduleList.forEach {

					}
				}
	}
}
