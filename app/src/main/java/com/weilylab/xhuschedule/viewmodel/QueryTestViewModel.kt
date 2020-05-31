package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.repository.StudentRepository
import com.weilylab.xhuschedule.repository.TestRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.content
import vip.mystery0.rx.empty
import vip.mystery0.rx.launch

class QueryTestViewModel : ViewModel(), KoinComponent {
	private val studentRepository: StudentRepository by inject()
	private val testRepository: TestRepository by inject()

	val studentList by lazy { MutableLiveData<List<Student>>() }
	val student by lazy { MutableLiveData<Student>() }
	val testList by lazy { MutableLiveData<PackageData<List<Test>>>() }
	val html by lazy { MutableLiveData<String>() }

	fun init() {
		launch(testList) {
			val list = studentRepository.queryAllStudentList()
			val mainStudent = list.find { it.isMain }
			if (mainStudent == null) {
				student.postValue(null)
				return@launch
			}
			student.postValue(mainStudent)
			studentList.postValue(list)
		}
	}

	fun query(student: Student) {
		launch(testList) {
			val response = testRepository.queryTests(student)
			if (response.first.isNullOrEmpty()) {
				testList.empty()
			} else {
				testList.content(response.first)
			}
			html.postValue(response.second)
		}
	}
}