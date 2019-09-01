package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Classroom
import com.weilylab.xhuschedule.model.Student
import vip.mystery0.rx.PackageData

class QueryClassroomViewModel : ViewModel() {
	val classroomList by lazy { MutableLiveData<PackageData<List<Classroom>>>() }
	val student by lazy { MutableLiveData<PackageData<Student>>() }
	val location by lazy { MutableLiveData<String>() }
	val week by lazy { MutableLiveData<String>() }
	val day by lazy { MutableLiveData<String>() }
	val time by lazy { MutableLiveData<String>() }
}