package com.weilylab.xhuschedule.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData

class ClassCourseColorViewModel : ViewModel() {
	val classCourseList = MutableLiveData<PackageData<List<Course>>>()
}