package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.FeedBackToken
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.repository.ds.StudentDataSource
import com.weilylab.xhuschedule.repository.local.service.StudentService
import com.weilylab.xhuschedule.repository.local.service.impl.StudentServiceImpl
import com.weilylab.xhuschedule.repository.remote.StudentRemoteDataSource
import com.weilylab.xhuschedule.utils.RxObservable
import com.weilylab.xhuschedule.utils.RxObserver
import com.weilylab.xhuschedule.utils.userDo.UserUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.rx.PackageData
import java.util.*

object StudentLocalDataSource : StudentDataSource {
	private val studentService: StudentService by lazy { StudentServiceImpl() }

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

	fun queryMainStudent(studentLiveData: MutableLiveData<PackageData<Student>>) {
		studentLiveData.value = PackageData.loading()
		queryMainStudent {
			studentLiveData.value = it
		}
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
					it.onFinish(queryAllStudentListDo())
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

	/**
	 * 查询学生信息的同步方法，提供给小部件调用
	 */
	fun queryAllStudentListDo(): List<Student> = studentService.queryAllStudentList()

	fun queryAllStudentInfo(listener: (PackageData<List<StudentInfo>>) -> Unit) {
		RxObservable<List<StudentInfo>>()
				.doThings {
					it.onFinish(studentService.queryAllStudentInfo())
				}
				.subscribe(object : RxObserver<List<StudentInfo>>() {
					override fun onError(e: Throwable) {
						listener.invoke(PackageData.error(e))
					}

					override fun onFinish(data: List<StudentInfo>?) {
						if (data == null || data.isEmpty())
							listener.invoke(PackageData.empty())
						else
							listener.invoke(PackageData.content(data))
					}
				})
	}

	fun registerFeedBackToken(student: Student, feedBackToken: String) {
		var fbToken = studentService.queryFeedBackTokenForUsername(student.username)
		if (fbToken == null) {
			fbToken = FeedBackToken()
			fbToken.username = student.username
			fbToken.fbToken = feedBackToken
			studentService.registerFeedBackToken(fbToken)
		} else {
			fbToken.fbToken = feedBackToken
			studentService.updateFeedBackToken(fbToken)
		}
	}

	fun queryFeedBackTokenForUsername(student: Student, feedBackTokenLiveData: MutableLiveData<PackageData<String>>) {
		feedBackTokenLiveData.value = PackageData.loading()
		queryFeedBackTokenForUsername(student) {
			feedBackTokenLiveData.value = it
		}
	}

	fun queryFeedBackTokenForUsername(student: Student, listener: (PackageData<String>) -> Unit) {
		Observable.create<FeedBackToken> {
			val token = studentService.queryFeedBackTokenForUsername(student.username)
			if (token != null)
				it.onNext(token)
			it.onComplete()
		}
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : RxObserver<FeedBackToken>() {
					override fun onError(e: Throwable) {
						listener.invoke(PackageData.error(e))
					}

					override fun onFinish(data: FeedBackToken?) {
						if (data != null) {
							val tokenTimeString = data.fbToken.substring(data.fbToken.indexOf('_') + 1)
							val tokenTime = Calendar.getInstance()
							tokenTime.timeInMillis = tokenTimeString.toLong() - 30 * 60 * 1000
							if (tokenTime.timeInMillis > Calendar.getInstance().timeInMillis)
								listener.invoke(PackageData.content(data.fbToken))
							else
								UserUtil.login(student, null, object : RequestListener<Boolean> {
									override fun done(t: Boolean) {
										queryFeedBackTokenForUsername(student, listener)
									}

									override fun error(rt: String, msg: String?) {
										listener.invoke(PackageData.error(Exception(msg)))
									}
								})
						} else
							UserUtil.login(student, null, object : RequestListener<Boolean> {
								override fun done(t: Boolean) {
									queryFeedBackTokenForUsername(student, listener)
								}

								override fun error(rt: String, msg: String?) {
									listener.invoke(PackageData.error(Exception(msg)))
								}
							})
					}
				})
	}
}