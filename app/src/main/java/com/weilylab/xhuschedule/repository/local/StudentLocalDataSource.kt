package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.repository.dataSource.StudentDataSource
import com.weilylab.xhuschedule.repository.local.service.StudentService
import com.weilylab.xhuschedule.repository.local.service.impl.StudentServiceImpl
import com.weilylab.xhuschedule.repository.remote.StudentRemoteDataSource
import io.reactivex.Observer
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.rx.RxObservable
import vip.mystery0.rxpackagedata.rx.RxObserver

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
					val studentInfo = studentService.queryStudentInfoByUsername(student.username)
					if (studentInfo == null)
						it.onError(Exception(StringConstant.hint_data_null))
					else
						it.onFinish(studentInfo)
				}
				.subscribe(object : RxObserver<StudentInfo>() {
					override fun onFinish(data: StudentInfo?) {
						studentInfoLiveData.value = PackageData.content(data)
					}

					override fun onError(e: Throwable) {
						StudentRemoteDataSource.queryStudentInfo(studentInfoLiveData, student)
					}
				})
	}

	fun queryManyStudentInfo(studentInfoLiveData: MutableLiveData<PackageData<Map<Student, StudentInfo?>>>, studentList: List<Student>) {
		studentInfoLiveData.value = PackageData.loading()
		RxObservable<Map<Student, StudentInfo?>>()
				.doThings { emitter ->
					val map = HashMap<Student, StudentInfo?>()
					studentList.forEach {
						map[it] = studentService.queryStudentInfoByUsername(it.username)
					}
					emitter.onFinish(map)
				}
				.subscribe(object : RxObserver<Map<Student, StudentInfo?>>() {
					override fun onFinish(data: Map<Student, StudentInfo?>?) {
						studentInfoLiveData.value = PackageData.content(data)
					}

					override fun onError(e: Throwable) {
						studentInfoLiveData.value = PackageData.error(e)
					}
				})
	}

	fun queryMainStudent(listener: (PackageData<Student>) -> Unit) {
		RxObservable<Student?>()
				.doThings {
					it.onFinish(studentService.queryMainStudent())
				}
				.subscribe(object : RxObserver<Student?>() {
					override fun onFinish(data: Student?) {
						if (data == null)
							listener.invoke(PackageData.empty(data))
						else
							listener.invoke(PackageData.content(data))
					}

					override fun onError(e: Throwable) {
						listener.invoke(PackageData.error(e))
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
				}
				.subscribe(observer)
	}

	fun updateStudent(studentList: List<Student>, observer: Observer<Boolean>) {
		RxObservable<Boolean>()
				.doThings { emitter ->
					studentList.forEach {
						studentService.updateStudent(it)
					}
					emitter.onFinish(true)
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
					it.onFinish(studentService.queryAllStudentList())
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