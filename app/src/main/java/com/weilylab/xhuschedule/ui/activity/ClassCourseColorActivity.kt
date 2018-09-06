package com.weilylab.xhuschedule.ui.activity

import androidx.lifecycle.Observer
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.ui.adapter.ClassCourseColorRecyclerViewAdapter
import com.weilylab.xhuschedule.viewModel.ClassCourseColorViewModel
import vip.mystery0.rxpackagedata.PackageData

class ClassCourseColorActivity : XhuBaseActivity(R.layout.activity_class_course_color) {
	private lateinit var classCourseColorViewModel: ClassCourseColorViewModel
	private lateinit var classCourseColorRecyclerViewAdapter: ClassCourseColorRecyclerViewAdapter

	private val classCourseColorObserver = Observer<PackageData<List<Course>>> {

	}
}
