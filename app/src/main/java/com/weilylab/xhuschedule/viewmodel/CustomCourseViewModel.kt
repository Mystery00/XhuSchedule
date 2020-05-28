package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.repository.CustomCourseRepository
import com.weilylab.xhuschedule.repository.StudentRepository
import com.weilylab.xhuschedule.utils.CalendarUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.content
import vip.mystery0.rx.launch
import vip.mystery0.rx.loading
import java.util.*
import kotlin.collections.HashMap

class CustomCourseViewModel : ViewModel(), KoinComponent {
	private val studentRepository: StudentRepository by inject()
	private val customCourseRepository: CustomCourseRepository by inject()

	val studentList by lazy { MutableLiveData<List<Student>>() }
	val studentInfoList by lazy { MutableLiveData<PackageData<Map<Student, StudentInfo?>>>() }
	val customCourseList by lazy { MutableLiveData<PackageData<List<Any>>>() }
	val syncCustomCourse by lazy { MutableLiveData<PackageData<Boolean>>() }
	val time by lazy { MutableLiveData<Pair<Int, Int>>() }
	val weekIndex by lazy { MutableLiveData<Int>() }
	val mainStudent by lazy { MutableLiveData<Student>() }
	val year by lazy { MutableLiveData<String>() }
	val term by lazy { MutableLiveData<String>() }

	fun init() {
		studentInfoList.loading()
		launch(customCourseList) {
			if (studentList.value == null) {
				studentList.postValue(studentRepository.queryAllStudentList())
			}
			var student: Student? = null
			val infoMap = HashMap<Student, StudentInfo?>()
			studentList.value?.forEach {
				val info = studentRepository.queryStudentInfo(it)
				infoMap[it] = info
				if (it.isMain) {
					student = it
					student?.username = info.name
				}
			}
			studentInfoList.content(infoMap)
			mainStudent.postValue(student)
			withContext(Dispatchers.Default) {
				year.postValue(CalendarUtil.getSelectArray(null).last())
				val now = Calendar.getInstance()
				now.firstDayOfWeek = Calendar.MONDAY
				val month = now.get(Calendar.MONTH)
				val week = now.get(Calendar.DAY_OF_WEEK)
				term.postValue(if (month in Calendar.MARCH until Calendar.SEPTEMBER) "2" else "1")
				weekIndex.postValue(week)
				time.postValue(Pair(1, 1))
			}
		}
	}

	fun getAllCustomCourse() {
		launch(customCourseList) {
			getAllCustomCourseInCoroutine()
		}
	}

	private suspend fun getAllCustomCourseInCoroutine() {
		customCourseList.content(customCourseRepository.getAll())
	}

	fun saveCustomCourse(course: Course, block: () -> Unit) {
		launch(customCourseList) {
			customCourseRepository.save(course)
			block()
		}
	}

	fun updateCustomCourse(course: Course, block: () -> Unit) {
		launch(customCourseList) {
			customCourseRepository.update(course)
			block()
		}
	}

	fun deleteCustomCourse(course: Course, block: () -> Unit) {
		launch(customCourseList) {
			customCourseRepository.delete(course)
			block()
		}
	}

	fun syncForLocal(student: Student) {
		launch(customCourseList) {
			customCourseList.content(customCourseRepository.syncCustomCourseForLocal(student))
		}
	}

	fun syncForRemote(student: Student) {
		launch(customCourseList) {
			customCourseList.content(customCourseRepository.syncCustomCourseForServer(student))
		}
	}
}