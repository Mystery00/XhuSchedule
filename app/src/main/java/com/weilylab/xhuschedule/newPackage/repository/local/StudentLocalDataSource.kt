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
import io.reactivex.Observer

object StudentLocalDataSource : StudentDataSource {
	private val studentService: StudentService = StudentServiceImpl()

	fun queryAllStudentList(studentListLiveData: MutableLiveData<PackageData<List<Student>>>) {
		studentListLiveData.value = PackageData.loading()
		queryAllStudentList {
			studentListLiveData.value = it
		}
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

	fun saveStudent(student: Student) {
		val mainStudent = studentService.queryMainStudent()
		if (mainStudent == null)
			student.isMain = true
		studentService.studentLogin(student)
	}

	fun deleteStudent(studentList: List<Student>, observer: Observer<Boolean>) {
		RxObservable<Boolean>()
				.doThings { emitter ->
					try {
						studentList.forEach {
							studentService.studentLogout(it)
						}
						val list = studentService.queryAllStudentList()
						if (list.isNotEmpty() && !checkMain(list)) {
							val mainStudent = list[0]
							mainStudent.isMain = true
							studentService.updateStudent(mainStudent)
						}
						emitter.onFinish(true)
					} catch (e: Exception) {
						emitter.onError(e)
					}
				}
				.subscribe(observer)
	}

	fun updateStudent(studentList: List<Student>, observer: Observer<Boolean>){
		RxObservable<Boolean>()
				.doThings { emitter ->
					try {
						studentList.forEach {
							studentService.updateStudent(it)
						}
						emitter.onFinish(true)
					} catch (e: Exception) {
						emitter.onError(e)
					}
				}
				.subscribe(observer)
	}

	private fun checkMain(list: List<Student>): Boolean {
		list.forEach {
			if (it.isMain)
				return true
		}
		return false
	}

	fun saveStudentInfo(studentInfo: StudentInfo) {
		val student = studentService.queryStudentByUsername(studentInfo.studentID)
		if (student != null) {
			student.studentName = studentInfo.name
			studentService.updateStudent(student)
		}
		studentService.saveStudentInfo(studentInfo)
	}

	fun queryAllStudentList(listener: (PackageData<List<Student>>) -> Unit) {
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
							listener.invoke(PackageData.empty(data))
						else
							listener.invoke(PackageData.content(data))
					}

					override fun onError(e: Throwable) {
						listener.invoke(PackageData.error(e))
					}
				})
	}
}