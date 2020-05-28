package com.weilylab.xhuschedule.repository.remote

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.listener.DoSaveListener
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.SyncCustomCourse
import com.weilylab.xhuschedule.model.response.GetUserDataResponse
import com.weilylab.xhuschedule.repository.ds.CourseDataSource
import com.weilylab.xhuschedule.repository.local.CourseLocalDataSource
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.userDo.CourseUtil
import com.weilylab.xhuschedule.utils.userDo.UserUtil
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.logs.Logs
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.StartAndCompleteObserver
import vip.mystery0.tools.factory.fromJson
import vip.mystery0.tools.utils.NetworkTools

object CourseRemoteDataSource : CourseDataSource {
	override fun queryCourseByUsername(courseListLiveData: MutableLiveData<PackageData<List<Schedule>>>, student: Student, year: String?, term: String?, isFromCache: Boolean, isShowError: Boolean) {
		if (NetworkTools.instance.isConnectInternet()) {
			CourseUtil.getCourse(student, year, term, object : DoSaveListener<List<Course>> {
				override fun doSave(t: List<Course>) {
					t.forEach {
						it.studentID = student.username
						if (year != null && term != null) {
							it.year = year
							it.term = term
						} else {
							it.year = ConfigurationUtil.currentYear
							it.term = ConfigurationUtil.currentTerm
						}
					}
					CourseLocalDataSource.saveCourseList(student.username, t, year, term)
				}
			}, object : RequestListener<List<Course>> {
				override fun done(t: List<Course>) {
					courseListLiveData.value = PackageData.content(CourseUtil.convertCourseToSchedule(t))
				}

				override fun error(rt: String, msg: String?) {
					Logs.em("error: ", msg)
					if (isShowError) {
						courseListLiveData.value = PackageData.error(Exception(msg))
						if (!isFromCache)
							CourseLocalDataSource.queryCourseByUsername(courseListLiveData, student, year, term, isFromCache, isShowError)
					}
				}
			})
		} else {
			if (isShowError) {
				courseListLiveData.value = PackageData.error(Exception(StringConstant.hint_network_error))
				if (!isFromCache)
					CourseLocalDataSource.queryCourseByUsername(courseListLiveData, student, year, term, isFromCache, isShowError)
			}
		}
	}

	fun syncCustomCourseForLocal(statusLiveData: MutableLiveData<PackageData<Boolean>>, student: Student, key: String) {
		statusLiveData.value = PackageData.loading()
		if (NetworkTools.instance.isConnectInternet()) {
			UserUtil.getUserData(student, key, object : DoSaveListener<GetUserDataResponse> {
				override fun doSave(t: GetUserDataResponse) {
					val sync = t.value.fromJson<SyncCustomCourse?>()
					CourseLocalDataSource.syncLocal(sync?.list?: emptyList(), student.username)
				}
			}, object : RequestListener<String> {
				override fun done(t: String) {
					statusLiveData.value = PackageData.content(true)
				}

				override fun error(rt: String, msg: String?) {
					statusLiveData.value = PackageData.error(Exception(msg))
				}
			})
		} else {
			statusLiveData.value = PackageData.error(Exception(StringConstant.hint_network_error))
		}
	}
}