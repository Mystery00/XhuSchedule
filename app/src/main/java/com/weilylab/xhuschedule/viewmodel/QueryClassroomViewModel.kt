package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.api.ClassRoomAPI
import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.model.Classroom
import com.weilylab.xhuschedule.model.Student
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.content
import vip.mystery0.rx.launch
import vip.mystery0.rx.loading

class QueryClassroomViewModel : ViewModel() {
	private val classRoomAPI = RetrofitFactory.retrofit.create(ClassRoomAPI::class.java)

	val classroomList by lazy { MutableLiveData<PackageData<List<Classroom>>>() }
	val student by lazy { MutableLiveData<PackageData<Student>>() }
	val location by lazy { MutableLiveData<String>() }
	val week by lazy { MutableLiveData<String>() }
	val day by lazy { MutableLiveData<String>() }
	val time by lazy { MutableLiveData<String>() }

	fun queryClassRoomList() {
		classroomList.loading()
		launch(classroomList) {
			val response = classRoomAPI.getClassrooms(location.value!!, week.value!!, day.value!!, time.value!!)
			when (response.rt) {
				ResponseCodeConstants.DONE -> classroomList.content(response.classrooms)

			}
		}
	}
}