package com.weilylab.xhuschedule.newPackage.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.model.Course
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.repository.BottomNavigationRepository
import com.weilylab.xhuschedule.newPackage.repository.dataSource.CourseDataSource
import com.weilylab.xhuschedule.newPackage.repository.local.service.impl.CourseServiceImpl
import com.weilylab.xhuschedule.newPackage.repository.remote.CourseRemoteDataSource
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObserver
import vip.mystery0.logs.Logs

object CourseLocalDataSource : CourseDataSource {
	private val courseService = CourseServiceImpl()

	override fun queryCourseByUsername(courseListLiveData: MutableLiveData<List<Course>>, messageLiveData: MutableLiveData<String>, requestCodeLiveData: MutableLiveData<Int>, student: Student, isFromCache: Boolean) {
		RxObservable<List<Course>>()
				.doThings {
					try {
						it.onFinish(courseService.queryCourseByUsername(student.username))
					} catch (e: Exception) {
						it.onError(e)
					}
				}
				.subscribe(object : RxObserver<List<Course>>() {
					override fun onFinish(data: List<Course>?) {
						if (data == null || data.isEmpty())
							CourseRemoteDataSource.queryCourseByUsername(courseListLiveData, messageLiveData, requestCodeLiveData, student, isFromCache)
						else {
							courseListLiveData.value = data
							requestCodeLiveData.value = BottomNavigationRepository.DONE
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						if (isFromCache) {
							CourseRemoteDataSource.queryCourseByUsername(courseListLiveData, messageLiveData, requestCodeLiveData, student, isFromCache)
						} else {
							messageLiveData.value = e.message
							requestCodeLiveData.value = BottomNavigationRepository.ERROR
						}
					}
				})
	}

	fun saveCourseList(courseList: List<Course>) {
		courseList.forEach {
			courseService.addCourse(it)
		}
	}
}