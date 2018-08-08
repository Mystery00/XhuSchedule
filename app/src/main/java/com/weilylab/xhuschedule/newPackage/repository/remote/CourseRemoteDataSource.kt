package com.weilylab.xhuschedule.newPackage.repository.remote

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.constant.StringConstant
import com.weilylab.xhuschedule.newPackage.listener.DoSaveListener
import com.weilylab.xhuschedule.newPackage.listener.RequestListener
import com.weilylab.xhuschedule.newPackage.model.Course
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.repository.dataSource.CourseDataSource
import com.weilylab.xhuschedule.newPackage.repository.local.CourseLocalDataSource
import com.weilylab.xhuschedule.newPackage.utils.CourseUtil
import com.weilylab.xhuschedule.newPackage.utils.NetworkUtil
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.logs.Logs

object CourseRemoteDataSource : CourseDataSource {
	override fun queryCourseByUsername(courseListLiveData: MutableLiveData<PackageData<List<Schedule>>>, student: Student, year: String?, term: String?, isFromCache: Boolean) {
		if (NetworkUtil.isConnectInternet()) {
			CourseUtil.getCourse(student, year, term, object : DoSaveListener<List<Course>> {
				override fun doSave(t: List<Course>) {
					Logs.i("doSave: ")
					CourseLocalDataSource.deleteAllCourseListForStudent(student.username, year, term)
					t.forEach {
						it.studentID = student.username
						if (year != null && term != null) {
							it.year = year
							it.term = term
						} else {
							it.year = "current"
							it.term = "current"
						}
					}
					CourseLocalDataSource.saveCourseList(t)
				}
			}, object : RequestListener<List<Course>> {
				override fun done(t: List<Course>) {
					Logs.i("done: ")
					courseListLiveData.value = PackageData.content(CourseUtil.convertCourseToSchedule(t))
				}

				override fun error(rt: String, msg: String?) {
					Logs.em("error: ", msg)
					courseListLiveData.value = PackageData.error(Exception(msg))
					if (!isFromCache)
						CourseLocalDataSource.queryCourseByUsername(courseListLiveData, student, year, term, isFromCache)
				}
			})
		} else {
			courseListLiveData.value = PackageData.error(Exception(StringConstant.hint_network_error))
			if (!isFromCache)
				CourseLocalDataSource.queryCourseByUsername(courseListLiveData, student, year, term, isFromCache)
		}
	}

	override fun queryCourseWithManyStudent(courseListLiveData: MutableLiveData<PackageData<List<Schedule>>>, studentList: List<Student>, year: String?, term: String?, isFromCache: Boolean) {
		if (NetworkUtil.isConnectInternet()) {
			CourseUtil.getCoursesForManyStudent(studentList, year, term, object : DoSaveListener<Map<String, List<Course>>> {
				override fun doSave(t: Map<String, List<Course>>) {
					Logs.i("doSave: ")
					val username = t.keys.first()
					val courseList = t[username]!!
					CourseLocalDataSource.deleteAllCourseListForStudent(username, year, term)
					courseList.forEach {
						it.studentID = username
						if (year != null && term != null) {
							it.year = year
							it.term = term
						} else {
							it.year = "current"
							it.term = "current"
						}
					}
					CourseLocalDataSource.saveCourseList(courseList)
				}
			}, object : RequestListener<List<Course>> {
				override fun done(t: List<Course>) {
					Logs.i("done: ")
					courseListLiveData.value = PackageData.content(CourseUtil.convertCourseToSchedule(t))
				}

				override fun error(rt: String, msg: String?) {
					Logs.em("error: ", msg)
					courseListLiveData.value = PackageData.error(Exception(msg))
					if (!isFromCache)
						CourseLocalDataSource.queryCourseWithManyStudent(courseListLiveData, studentList, year, term, isFromCache)
				}
			})
		} else {
			courseListLiveData.value = PackageData.error(Exception(StringConstant.hint_network_error))
			if (!isFromCache)
				CourseLocalDataSource.queryCourseWithManyStudent(courseListLiveData, studentList, year, term, isFromCache)
		}
	}
}