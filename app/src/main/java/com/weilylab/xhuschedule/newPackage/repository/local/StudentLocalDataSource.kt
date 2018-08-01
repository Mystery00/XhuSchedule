package com.weilylab.xhuschedule.newPackage.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.constant.StringConstant
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.StudentInfo
import com.weilylab.xhuschedule.newPackage.repository.dataSource.StudentDataSource
import com.weilylab.xhuschedule.newPackage.repository.local.service.StudentService
import com.weilylab.xhuschedule.newPackage.repository.local.service.impl.StudentServiceImpl
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObserver

object StudentLocalDataSource : StudentDataSource {
	private val studentService: StudentService = StudentServiceImpl()

	fun queryAllStudentList(studentListLiveData: MutableLiveData<PackageData<List<Student>>>) {
		studentListLiveData.value = PackageData.loading()
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
						if (data == null || data.isEmpty())
							studentListLiveData.value = PackageData.empty(data)
						else
							studentListLiveData.value = PackageData.content(data)
					}

					override fun onError(e: Throwable) {
						studentListLiveData.value = PackageData.error(e)
					}
				})
	}

	override fun queryStudentInfo(studentInfoLiveData: MutableLiveData<PackageData<StudentInfo>>, student: Student) {
		studentInfoLiveData.value = PackageData.loading()
		RxObservable<StudentInfo>()
				.doThings {
					try {
						val studentInfo = studentService.queryStudentInfoByUsername(student.username)
						if (studentInfo == null)
							it.onError(Exception(StringConstant.hint_data_null))
						else
							it.onFinish(studentInfo)
					} catch (e: Exception) {
						it.onError(e)
					}
				}
				.subscribe(object : RxObserver<StudentInfo>() {
					override fun onFinish(data: StudentInfo?) {
						studentInfoLiveData.value = PackageData.content(data)
					}

					override fun onError(e: Throwable) {
						studentInfoLiveData.value = PackageData.error(e)
					}
				})
	}

	fun saveStudent(student: Student) = studentService.studentLogin(student)


	fun saveStudentInfo(studentInfo: StudentInfo) = studentService.saveStudentInfo(studentInfo)
}