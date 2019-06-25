package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.ds.CourseDataSource
import com.weilylab.xhuschedule.repository.local.service.CourseService
import com.weilylab.xhuschedule.repository.local.service.impl.CourseServiceImpl
import com.weilylab.xhuschedule.repository.remote.CourseRemoteDataSource
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.userDo.CourseUtil
import com.zhuangfei.timetable.model.Schedule
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs
import vip.mystery0.rx.DoNothingObserver
import vip.mystery0.rx.OnlyCompleteObserver
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.StartAndCompleteObserver

object CourseLocalDataSource : CourseDataSource {
	private val courseService: CourseService by lazy { CourseServiceImpl() }

	override fun queryCourseByUsername(courseListLiveData: MutableLiveData<PackageData<List<Schedule>>>, student: Student, year: String?, term: String?, isFromCache: Boolean, isShowError: Boolean) {
		Observable.create<List<Schedule>> {
			it.onNext(getRowCourseList(student, year, term))
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : StartAndCompleteObserver<List<Schedule>>() {
					override fun onSubscribe(d: Disposable) {
						courseListLiveData.value = PackageData.loading()
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						if (isFromCache) {
							CourseRemoteDataSource.queryCourseByUsername(courseListLiveData, student, year, term, isFromCache, isShowError)
						} else {
							courseListLiveData.value = PackageData.error(e)
						}
					}

					override fun onFinish(data: List<Schedule>?) {
						if (data == null || data.isEmpty())
							CourseRemoteDataSource.queryCourseByUsername(courseListLiveData, student, year, term, isFromCache, isShowError)
						else {
							courseListLiveData.value = PackageData.content(data)
						}
					}
				})
	}

	override fun queryCourseWithManyStudent(courseListLiveData: MutableLiveData<PackageData<List<Schedule>>>, studentList: List<Student>, year: String?, term: String?, isFromCache: Boolean, isShowError: Boolean) {
		Observable.create<List<Schedule>> {
			val courses = ArrayList<Schedule>()
			studentList.forEach { s -> courses.addAll(getRowCourseList(s, year, term)) }
			it.onNext(courses)
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : StartAndCompleteObserver<List<Schedule>>() {
					override fun onSubscribe(d: Disposable) {
						courseListLiveData.value = PackageData.loading()
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						if (isFromCache) {
							CourseRemoteDataSource.queryCourseWithManyStudent(courseListLiveData, studentList, year, term, isFromCache, isShowError)
						} else {
							courseListLiveData.value = PackageData.error(e)
						}
					}

					override fun onFinish(data: List<Schedule>?) {
						if (data == null || data.isEmpty())
							CourseRemoteDataSource.queryCourseWithManyStudent(courseListLiveData, studentList, year, term, isFromCache, isShowError)
						else {
							courseListLiveData.value = PackageData.content(data)
						}
					}
				})
	}

	fun queryDistinctCourseByUsernameAndTerm(courseListLiveData: MutableLiveData<PackageData<List<Course>>>) {
		Observable.create<List<Course>> {
			it.onNext(courseService.queryDistinctCourseByUsernameAndTerm())
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : StartAndCompleteObserver<List<Course>>() {
					override fun onError(e: Throwable) {
						courseListLiveData.value = PackageData.error(e)
					}

					override fun onFinish(data: List<Course>?) {
						if (data == null || data.isEmpty())
							courseListLiveData.value = PackageData.empty()
						else
							courseListLiveData.value = PackageData.content(data)
					}

					override fun onSubscribe(d: Disposable) {
						courseListLiveData.value = PackageData.loading()
					}
				})
	}

	fun getDistinctRowCourseList(): List<Course> = courseService.queryDistinctCourseByUsernameAndTerm()

	fun updateCourseColor(course: Course, color: String) {
		Observable.create<Boolean> {
			val list = courseService.queryCourseByName(course.name)
			list.forEach { c ->
				c.color = color
				courseService.updateCourse(c)
			}
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(DoNothingObserver<Boolean>())
	}

	fun getRowCourseList(student: Student, year: String? = null, term: String? = null): List<Schedule> {
		val courses = courseService.queryCourseByUsernameAndTerm(student.username, year
				?: ConfigurationUtil.currentYear, term ?: ConfigurationUtil.currentTerm)
		val custom = courseService.queryCustomCourseByTerm(student.username, year
				?: ConfigurationUtil.currentYear, term ?: ConfigurationUtil.currentTerm)
		val all = ArrayList<Course>()
		all.addAll(courses)
		all.addAll(custom)
		return CourseUtil.convertCourseToSchedule(all)
	}

	fun getCustomCourseList(student: Student, year: String? = null, term: String? = null): List<Course> = getCustomCourseListByUsername(student.username, year, term)

	fun getCustomCourseListByUsername(username: String, year: String? = null, term: String? = null): List<Course> = courseService.queryCustomCourseByTerm(username, year
			?: ConfigurationUtil.currentYear, term ?: ConfigurationUtil.currentTerm)

	fun getCustomCourseListByStudent(username: String): List<Course> = courseService.queryCustomCourseByStudent(username)

	fun saveCourseList(username: String, courseList: List<Course>, year: String? = null, term: String? = null) {
		val savedList = courseService.queryCourseByUsernameAndTerm(username, year
				?: ConfigurationUtil.currentYear, term ?: ConfigurationUtil.currentTerm)
		savedList.forEach {
			courseService.deleteCourse(it)
		}
		courseList.forEach { course ->
			course.color = ""
			val has = savedList.find { it.name == course.name }
			if (has != null)
				course.color = has.color
			courseService.addCourse(course)
		}
		ConfigurationUtil.lastUpdateDate = CalendarUtil.getTodayDateString()
	}

	/**
	 * 该接口提供给自定义课程页面使用
	 */
	fun getAll(customCourseLiveData: MutableLiveData<PackageData<List<Any>>>) {
		Observable.create<List<Course>> {
			it.onNext(courseService.queryAllCustomCourse())
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.computation())
				.map {
					val list = ArrayList<Course>()
					list.addAll(it)
					val map = list.groupBy { c -> c.studentID }
					val result = ArrayList<Any>()
					for (key in map.keys) {
						result.add(key)
						map.getValue(key).sortedBy { c -> c.name }.forEach { c -> result.add(c) }
					}
					result
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<List<Any>>() {
					override fun onError(e: Throwable) {
						customCourseLiveData.value = PackageData.error(e)
					}

					override fun onFinish(data: List<Any>?) {
						when {
							data == null -> customCourseLiveData.value = PackageData.error(Exception("data is null"))
							data.isEmpty() -> customCourseLiveData.value = PackageData.empty(data)
							else -> customCourseLiveData.value = PackageData.content(data)
						}
					}
				})
	}

	fun save(course: Course, listener: (Boolean, Throwable?) -> Unit) {
		Observable.create<Boolean> {
			courseService.addCourse(course)
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<Boolean>() {
					override fun onError(e: Throwable) {
						listener.invoke(false, e)
					}

					override fun onFinish(data: Boolean?) {
						listener.invoke(true, null)
					}
				})
	}

	fun update(course: Course, listener: (Boolean, Throwable?) -> Unit) {
		Observable.create<Boolean> {
			courseService.updateCourse(course)
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<Boolean>() {
					override fun onError(e: Throwable) {
						listener.invoke(false, e)
					}

					override fun onFinish(data: Boolean?) {
						listener.invoke(true, null)
					}
				})
	}

	fun delete(course: Course, listener: (Boolean) -> Unit) {
		Observable.create<Boolean> {
			courseService.deleteCourse(course)
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<Boolean>() {
					override fun onError(e: Throwable) {
						listener.invoke(false)
					}

					override fun onFinish(data: Boolean?) {
						listener.invoke(true)
					}
				})
	}

	/**
	 * 保存同步的数据
	 */
	fun syncLocal(courseList: List<Course>, username: String) {
		val savedList = courseService.queryCustomCourseByStudent(username)
		savedList.forEach { c ->
			courseService.deleteCourse(c)
		}
		courseList.forEach { course ->
			course.studentID = username
			courseService.addCourse(course)
		}
	}
}