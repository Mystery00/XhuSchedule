package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.ds.CourseDataSource
import com.weilylab.xhuschedule.repository.local.service.CourseService
import com.weilylab.xhuschedule.repository.local.service.impl.CourseServiceImpl
import com.weilylab.xhuschedule.repository.remote.CourseRemoteDataSource
import com.weilylab.xhuschedule.utils.*
import com.weilylab.xhuschedule.utils.userDo.CourseUtil
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.logs.Logs
import vip.mystery0.rx.PackageData

object CourseLocalDataSource : CourseDataSource {
	private val courseService: CourseService by lazy { CourseServiceImpl() }

	override fun queryCourseByUsername(courseListLiveData: MutableLiveData<PackageData<List<Schedule>>>, student: Student, year: String?, term: String?, isFromCache: Boolean, isShowError: Boolean) {
		RxObservable<List<Schedule>>()
				.doThings {
					it.onFinish(getRowCourseList(student, year, term))
				}
				.subscribe(object : RxObserver<List<Schedule>>() {
					override fun onFinish(data: List<Schedule>?) {
						if (data == null || data.isEmpty())
							CourseRemoteDataSource.queryCourseByUsername(courseListLiveData, student, year, term, isFromCache, isShowError)
						else {
							courseListLiveData.value = PackageData.content(data)
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						if (isFromCache) {
							CourseRemoteDataSource.queryCourseByUsername(courseListLiveData, student, year, term, isFromCache, isShowError)
						} else {
							courseListLiveData.value = PackageData.error(e)
						}
					}
				})
	}

	override fun queryCourseWithManyStudent(courseListLiveData: MutableLiveData<PackageData<List<Schedule>>>, studentList: List<Student>, year: String?, term: String?, isFromCache: Boolean, isShowError: Boolean) {
		RxObservable<List<Schedule>>()
				.doThings { emitter ->
					val courses = ArrayList<Schedule>()
					studentList.forEach { courses.addAll(getRowCourseList(it, year, term)) }
					emitter.onFinish(courses)
				}
				.subscribe(object : RxObserver<List<Schedule>>() {
					override fun onFinish(data: List<Schedule>?) {
						if (data == null || data.isEmpty())
							CourseRemoteDataSource.queryCourseWithManyStudent(courseListLiveData, studentList, year, term, isFromCache, isShowError)
						else {
							courseListLiveData.value = PackageData.content(data)
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						if (isFromCache) {
							CourseRemoteDataSource.queryCourseWithManyStudent(courseListLiveData, studentList, year, term, isFromCache, isShowError)
						} else {
							courseListLiveData.value = PackageData.error(e)
						}
					}
				})
	}

	fun queryDistinctCourseByUsernameAndTerm(courseListLiveData: MutableLiveData<PackageData<List<Course>>>) {
		courseListLiveData.value = PackageData.loading()
		RxObservable<List<Course>>()
				.doThings {
					it.onFinish(courseService.queryDistinctCourseByUsernameAndTerm())
				}
				.subscribe(object : RxObserver<List<Course>>() {
					override fun onFinish(data: List<Course>?) {
						if (data == null || data.isEmpty())
							courseListLiveData.value = PackageData.empty()
						else
							courseListLiveData.value = PackageData.content(data)
					}

					override fun onError(e: Throwable) {
						courseListLiveData.value = PackageData.error(e)
					}
				})
	}

	fun getDistinctRowCourseList(): List<Course> = courseService.queryDistinctCourseByUsernameAndTerm()

	fun updateCourseColor(course: Course, color: String) {
		RxObservable<Boolean>()
				.doThings { observableEmitter ->
					val list = courseService.queryCourseByName(course.name)
					list.forEach {
						it.color = color
						courseService.updateCourse(it)
					}
					observableEmitter.onFinish(true)
				}
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

	fun getCustomCourseList(student: Student, year: String? = null, term: String? = null): List<Course> {
		return courseService.queryCustomCourseByTerm(student.username, year
				?: ConfigurationUtil.currentYear, term ?: ConfigurationUtil.currentTerm)
	}

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
	fun getAll(customThingLiveData: MutableLiveData<PackageData<List<Course>>>) {
		RxObservable<List<Course>>()
				.doThings { observableEmitter ->
					observableEmitter.onFinish(courseService.queryAllCustomCourse())
				}
				.subscribe(object : RxObserver<List<Course>>() {
					override fun onError(e: Throwable) {
						customThingLiveData.value = PackageData.error(e)
					}

					override fun onFinish(data: List<Course>?) {
						when {
							data == null -> customThingLiveData.value = PackageData.error(Exception("data is null"))
							data.isEmpty() -> customThingLiveData.value = PackageData.empty(data)
							else -> customThingLiveData.value = PackageData.content(data)
						}
					}
				})
	}

	fun save(course: Course, listener: (Boolean, Throwable?) -> Unit) {
		RxObservable<Boolean>()
				.doThings {
					courseService.addCourse(course)
					it.onFinish(true)
				}
				.subscribe(object : RxObserver<Boolean>() {
					override fun onError(e: Throwable) {
						listener.invoke(false, e)
					}

					override fun onFinish(data: Boolean?) {
						listener.invoke(data != null && data, null)
					}
				})
	}

	fun update(course: Course, listener: (Boolean, Throwable?) -> Unit) {
		RxObservable<Boolean>()
				.doThings {
					courseService.updateCourse(course)
					it.onFinish(true)
				}
				.subscribe(object : RxObserver<Boolean>() {
					override fun onError(e: Throwable) {
						listener.invoke(false, e)
					}

					override fun onFinish(data: Boolean?) {
						listener.invoke(data != null && data, null)
					}
				})
	}

	fun delete(course: Course, listener: (Boolean) -> Unit) {
		RxObservable<Boolean>()
				.doThings {
					courseService.deleteCourse(course)
					it.onFinish(true)
				}
				.subscribe(object : RxObserver<Boolean>() {
					override fun onError(e: Throwable) {
						listener.invoke(false)
					}

					override fun onFinish(data: Boolean?) {
						listener.invoke(data != null && data)
					}
				})
	}
}