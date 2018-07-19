package com.weilylab.xhuschedule.newPackage.repository

import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.repository.local.CourseLocalDataSource
import com.weilylab.xhuschedule.newPackage.repository.remote.CourseRemoteDataSource
import com.weilylab.xhuschedule.newPackage.viewModel.BottomNavigationViewModel

object CourseRepository {
	const val DONE = 21
	const val ERROR = 22

	fun getCourseByStudent(student: Student, bottomNavigationViewModel: BottomNavigationViewModel) = CourseRemoteDataSource.queryCourseByUsername(bottomNavigationViewModel.courseList, bottomNavigationViewModel.message, bottomNavigationViewModel.requestCode, student, false)

	fun getCourseCacheByStudent(student: Student, bottomNavigationViewModel: BottomNavigationViewModel) = CourseLocalDataSource.queryCourseByUsername(bottomNavigationViewModel.courseList, bottomNavigationViewModel.message, bottomNavigationViewModel.requestCode, student, true)
}