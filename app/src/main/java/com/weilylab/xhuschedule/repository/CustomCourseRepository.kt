package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.local.CourseLocalDataSource
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.repository.remote.CourseRemoteDataSource
import com.weilylab.xhuschedule.viewmodel.CustomCourseViewModel
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.Status.*

object CustomCourseRepository {
	fun getAll(customCourseViewModel: CustomCourseViewModel) = CourseLocalDataSource.getAll(customCourseViewModel.customCourseList)

	fun save(course: Course, listener: (Boolean, Throwable?) -> Unit) = CourseLocalDataSource.save(course, listener)

	fun update(course: Course, listener: (Boolean, Throwable?) -> Unit) = CourseLocalDataSource.update(course, listener)

	fun delete(course: Course, listener: (Boolean) -> Unit) = CourseLocalDataSource.delete(course, listener)

	fun queryAllStudentInfo(scoreViewModel: CustomCourseViewModel) {
		scoreViewModel.studentInfoList.value = PackageData.loading()
		scoreViewModel.studentInfoList.removeSource(scoreViewModel.studentList)
		scoreViewModel.studentInfoList.addSource(scoreViewModel.studentList) {
			when (it.status) {
				Content -> if (it.data!!.isNotEmpty())
					StudentLocalDataSource.queryManyStudentInfo(scoreViewModel.studentInfoList, it.data!!)
				Error -> scoreViewModel.studentInfoList.value = PackageData.error(it.error)
				Empty -> scoreViewModel.studentInfoList.value = PackageData.empty()
				Loading -> scoreViewModel.studentInfoList.value = PackageData.loading()
			}
		}
		StudentLocalDataSource.queryAllStudentList(scoreViewModel.studentList)
	}

	fun syncCustomCourseForLocal(customCourseViewModel: CustomCourseViewModel, student: Student) = CourseRemoteDataSource.syncCustomCourseForLocal(customCourseViewModel.syncCustomCourse, student, "customCourse")

	fun syncCustomCourseForServer(customCourseViewModel: CustomCourseViewModel, student: Student) = CourseRemoteDataSource.syncCustomCourseForServer(customCourseViewModel.syncCustomCourse, student, "customCourse")
}