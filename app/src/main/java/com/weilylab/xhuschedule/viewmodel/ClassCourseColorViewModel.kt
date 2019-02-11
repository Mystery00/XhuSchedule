package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Course
import vip.mystery0.rxpackagedata.PackageData

class ClassCourseColorViewModel : ViewModel() {
	val classCourseList by lazy { MutableLiveData<PackageData<List<Course>>>() }
}