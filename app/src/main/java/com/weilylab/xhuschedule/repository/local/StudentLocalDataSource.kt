package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.FeedBackToken
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.repository.ds.StudentDataSource
import com.weilylab.xhuschedule.repository.remote.StudentRemoteDataSource
import com.weilylab.xhuschedule.utils.userDo.UserUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.rx.OnlyCompleteObserver
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.StartAndCompleteObserver
import java.util.*

class StudentLocalDataSource(
		private val studentService: StudentService
) : StudentDataSource {

	fun queryAllStudentList(studentListLiveData: MutableLiveData<PackageData<List<Student>>>) {
		studentListLiveData.value = PackageData.loading()
		queryAllStudentList {
			studentListLiveData.value = it
		}
	}

	override fun queryStudentInfo(studentInfoLiveData: MutableLiveData<PackageData<StudentInfo>>, student: Student) {
		Observable.create<StudentInfo> {
			val studentInfo = studentService.queryStudentInfoByUsername(student.username)
			if (studentInfo == null)
				it.onError(Exception(StringConstant.hint_data_null))
			else
				it.onNext(studentInfo)
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : StartAndCompleteObserver<StudentInfo>() {
					override fun onSubscribe(d: Disposable) {
						studentInfoLiveData.value = PackageData.loading()
					}

					override fun onError(e: Throwable) {
						StudentRemoteDataSource.queryStudentInfo(studentInfoLiveData, student)
					}

					override fun onFinish(data: StudentInfo?) {
						studentInfoLiveData.value = PackageData.content(data)
					}
				})
	}

	fun queryManyStudentInfo(studentInfoLiveData: MutableLiveData<PackageData<Map<Student, StudentInfo?>>>, studentList: List<Student>) {
		Observable.create<Map<Student, StudentInfo?>> {
			val map = HashMap<Student, StudentInfo?>()
			studentList.forEach { s ->
				map[s] = studentService.queryStudentInfoByUsername(s.username)
			}
			it.onNext(map)
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : StartAndCompleteObserver<Map<Student, StudentInfo?>>() {
					override fun onError(e: Throwable) {
						studentInfoLiveData.value = PackageData.error(e)
					}

					override fun onFinish(data: Map<Student, StudentInfo?>?) {
						studentInfoLiveData.value = PackageData.content(data)
					}

					override fun onSubscribe(d: Disposable) {
						studentInfoLiveData.value = PackageData.loading()
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
		Observable.create<Student> {
			val student = queryMainStudent()
			if (student != null)
				it.onNext(student)
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<Student>() {
					override fun onError(e: Throwable) {
						listener.invoke(PackageData.error(e))
					}

					override fun onFinish(data: Student?) {
						if (data == null)
							listener.invoke(PackageData.empty(data))
						else
							listener.invoke(PackageData.content(data))
					}
				})
	}

	fun queryMainStudent(): Student? = studentService.queryMainStudent()

	fun saveStudent(student: Student) {
		val mainStudent = studentService.queryMainStudent()
		if (mainStudent == null)
			student.isMain = true
		studentService.studentLogin(student)
	}

	fun deleteStudent(studentList: List<Student>, observer: Observer<Boolean>) {
		Observable.create<Boolean> {
			studentList.forEach { s ->
				studentService.studentLogout(s)
			}
			val list = studentService.queryAllStudentList()
			if (list.isNotEmpty() && !checkMain(list)) {
				val mainStudent = list[0]
				mainStudent.isMain = true
				studentService.updateStudent(mainStudent)
			}
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer)
	}

	fun updateStudent(studentList: List<Student>, observer: Observer<Boolean>) {
		Observable.create<Boolean> {
			studentList.forEach { s ->
				studentService.updateStudent(s)
			}
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
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
		Observable.create<List<Student>> {
			it.onNext(queryAllStudentListDo())
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<List<Student>>() {
					override fun onError(e: Throwable) {
						listener.invoke(PackageData.error(e))
					}

					override fun onFinish(data: List<Student>?) {
						if (data == null || data.isEmpty())
							listener.invoke(PackageData.empty(data))
						else
							listener.invoke(PackageData.content(data))
					}
				})
	}

	/**
	 * 查询学生信息的同步方法，提供给小部件调用
	 */
	fun queryAllStudentListDo(): List<Student> = studentService.queryAllStudentList()

	fun queryAllStudentInfo(listener: (PackageData<List<StudentInfo>>) -> Unit) {
		Observable.create<List<StudentInfo>> {
			it.onNext(studentService.queryAllStudentInfo())
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<List<StudentInfo>>() {
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
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.computation())
				.map {
					val tokenTimeString = it.fbToken.substring(it.fbToken.indexOf('_') + 1)
					val tokenTime = Calendar.getInstance()
					tokenTime.timeInMillis = tokenTimeString.toLong() - 30 * 60 * 1000
					if (tokenTime.timeInMillis > Calendar.getInstance().timeInMillis)
						it.fbToken
					else
						"null"
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<String>() {
					override fun onError(e: Throwable) {
						listener.invoke(PackageData.error(e))
					}

					override fun onFinish(data: String?) {
						if (data != null) {
							if (data != "null")
								listener.invoke(PackageData.content(data))
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