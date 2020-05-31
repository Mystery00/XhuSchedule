package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.api.ClassRoomAPI
import com.weilylab.xhuschedule.model.Classroom
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.module.redoAfterLogin
import com.weilylab.xhuschedule.repository.StudentRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.rx.*

class QueryClassroomViewModel : ViewModel(), KoinComponent {
	private val classRoomAPI: ClassRoomAPI by inject()

	private val studentRepository: StudentRepository by inject()

	val classroomList by lazy { MutableLiveData<PackageData<List<Classroom>>>() }
	val student by lazy { MutableLiveData<Student>() }
	val location by lazy { MutableLiveData<String>() }
	val week by lazy { MutableLiveData<String>() }
	val day by lazy { MutableLiveData<String>() }
	val time by lazy { MutableLiveData<String>() }

	fun init() {
		launch(classroomList) {
			val mainStudent = studentRepository.queryMainStudent()
			if (mainStudent == null) {
				student.postValue(null)
				return@launch
			}
			student.postValue(mainStudent)
		}
	}

	private suspend fun queryClassRoomListInCoroutine(student: Student,
													  location: String,
													  week: String,
													  day: String,
													  time: String): List<Classroom> {
		val response = classRoomAPI.getClassrooms(student.username, location, week, day, time).redoAfterLogin(student) {
			classRoomAPI.getClassrooms(student.username, location, week, day, time)
		}
		if (!response.isSuccessful) {
			throw Exception(response.msg)
		}
		return response.classrooms
	}

	fun queryClassRoomList(student: Student,
						   location: String,
						   week: String,
						   day: String,
						   time: String) {
		classroomList.loading()
		launch(classroomList) {
			val list = queryClassRoomListInCoroutine(student, location, week, day, time)
			if (list.isNullOrEmpty()) {
				classroomList.empty()
			} else {
				classroomList.content(list)
			}
		}
	}
}