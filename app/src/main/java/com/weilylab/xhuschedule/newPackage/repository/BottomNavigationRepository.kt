package com.weilylab.xhuschedule.newPackage.repository

import com.weilylab.xhuschedule.newPackage.config.Status.*
import com.weilylab.xhuschedule.newPackage.repository.local.CourseLocalDataSource
import com.weilylab.xhuschedule.newPackage.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.newPackage.repository.remote.InitRemoteDataSource
import com.weilylab.xhuschedule.newPackage.repository.remote.StudentRemoteDataSource
import com.weilylab.xhuschedule.newPackage.utils.CalendarUtil
import com.weilylab.xhuschedule.newPackage.utils.CourseUtil
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.newPackage.viewModel.BottomNavigationViewModel

object BottomNavigationRepository {
	const val ACTION_NONE = 30
	const val ACTION_REFRESH = 31

	fun queryStudentInfo(bottomNavigationViewModel: BottomNavigationViewModel) {
		bottomNavigationViewModel.studentInfo.value = PackageData.loading()
		if (bottomNavigationViewModel.studentList.value == null || bottomNavigationViewModel.studentList.value?.data == null || bottomNavigationViewModel.studentList.value!!.data!!.isEmpty())
			bottomNavigationViewModel.courseList.value = PackageData.empty()
		else {
			StudentRemoteDataSource.queryStudentInfo(bottomNavigationViewModel.studentInfo, bottomNavigationViewModel.studentList.value!!.data!![0])
		}
	}

	fun queryCurrentWeek(bottomNavigationViewModel: BottomNavigationViewModel) {
		bottomNavigationViewModel.currentWeek.value = PackageData.loading()
		bottomNavigationViewModel.currentWeek.addSource(bottomNavigationViewModel.startDateTime) {
			when (it.status) {
				Content -> if (it.data != null) {
					CalendarUtil.startDateTime = it.data
					val week = CalendarUtil.getWeekFromCalendar(it.data)
					bottomNavigationViewModel.currentWeek.value = PackageData.content(week)
					bottomNavigationViewModel.week.value = week
				}
				Loading -> bottomNavigationViewModel.currentWeek.value = PackageData.loading()
				Empty -> bottomNavigationViewModel.currentWeek.value = PackageData.empty()
				Error -> bottomNavigationViewModel.currentWeek.value = PackageData.error(it.error)
			}
		}
		InitRemoteDataSource.getStartDateTime(bottomNavigationViewModel.startDateTime)
	}

	fun queryCacheCourses(bottomNavigationViewModel: BottomNavigationViewModel) {
		bottomNavigationViewModel.courseList.value = PackageData.loading()
		if (bottomNavigationViewModel.studentList.value == null || bottomNavigationViewModel.studentList.value?.data == null || bottomNavigationViewModel.studentList.value!!.data!!.isEmpty())
			bottomNavigationViewModel.courseList.value = PackageData.empty()
		else {
			bottomNavigationViewModel.todayCourseList.addSource(bottomNavigationViewModel.courseList) {
				when (it.status) {
					Content -> {
						CourseUtil.getTodayCourse(it.data!!) {
							bottomNavigationViewModel.todayCourseList.value = PackageData.content(it)
						}
					}
					Loading -> bottomNavigationViewModel.todayCourseList.value = PackageData.loading()
					Empty -> bottomNavigationViewModel.todayCourseList.value = PackageData.empty()
					Error -> bottomNavigationViewModel.todayCourseList.value = PackageData.error(it.error)
				}
			}
			CourseLocalDataSource.queryCourseByUsername(bottomNavigationViewModel.courseList, bottomNavigationViewModel.studentList.value!!.data!![0], "current", "current", true)
		}
	}

	fun queryStudentList(bottomNavigationViewModel: BottomNavigationViewModel) {
		bottomNavigationViewModel.studentList.value = PackageData.loading()
		StudentLocalDataSource.queryAllStudentList(bottomNavigationViewModel.studentList)
	}
}