package com.weilylab.xhuschedule.newPackage.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.constant.StringConstant
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.StudentInfo
import com.weilylab.xhuschedule.newPackage.repository.BottomNavigationRepository
import com.weilylab.xhuschedule.newPackage.repository.dataSource.StudentDataSource
import com.weilylab.xhuschedule.newPackage.repository.local.service.StudentService
import com.weilylab.xhuschedule.newPackage.repository.local.service.StudentServiceImpl
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObserver

object StudentLocalDataSource : StudentDataSource {
	private val studentService: StudentService = StudentServiceImpl()

	fun queryAllStudentList(studentListLiveData: MutableLiveData<List<Student>>, messageLiveData: MutableLiveData<String>, requestCodeLiveData: MutableLiveData<Int>) {
		RxObservable<List<Student>>()
				.doThings {
					try {
						it.onFinish(studentService.queryAllStudentList())
					} catch (e: Exception) {
						it.onError(e)
					}
				}
				.subscribe(object : RxObserver<List<Student>>() {
					override fun onFinish(data: List<Student>?) {
						requestCodeLiveData.value = BottomNavigationRepository.DONE
						studentListLiveData.value = data
					}

					override fun onError(e: Throwable) {
						messageLiveData.value = e.message
						requestCodeLiveData.value = BottomNavigationRepository.ERROR
					}
				})
	}

	override fun queryStudentInfo(studentInfoLiveData: MutableLiveData<StudentInfo>, messageLiveData: MutableLiveData<String>, requestCodeLiveData: MutableLiveData<Int>, student: Student) {
		RxObservable<StudentInfo>()
				.doThings {
					try {
						val studentInfo = studentService.queryStudentInfoByUsername(student.username)
						if (studentInfo == null) {
							messageLiveData.value = StringConstant.hint_data_null
							requestCodeLiveData.value = BottomNavigationRepository.ERROR
							return@doThings
						}
						it.onFinish(studentInfo)
					} catch (e: Exception) {
						it.onError(e)
					}
				}
				.subscribe(object : RxObserver<StudentInfo>() {
					override fun onFinish(data: StudentInfo?) {
						requestCodeLiveData.value = BottomNavigationRepository.DONE
						studentInfoLiveData.value = data
					}

					override fun onError(e: Throwable) {
						messageLiveData.value = e.message
						requestCodeLiveData.value = BottomNavigationRepository.ERROR
					}
				})
	}

	fun saveStudent(student: Student) = studentService.studentLogin(student)


	fun saveStudentInfo(studentInfo: StudentInfo) = studentService.saveStudentInfo(studentInfo)
}