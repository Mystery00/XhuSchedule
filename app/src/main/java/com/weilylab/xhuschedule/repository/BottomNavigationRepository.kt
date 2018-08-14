package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.config.Status.*
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.local.CourseLocalDataSource
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.repository.remote.CourseRemoteDataSource
import com.weilylab.xhuschedule.repository.remote.InitRemoteDataSource
import com.weilylab.xhuschedule.repository.remote.StudentRemoteDataSource
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.CourseUtil
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.viewModel.BottomNavigationViewModel

object BottomNavigationRepository {
	fun queryStudentInfo(bottomNavigationViewModel: BottomNavigationViewModel) {
		bottomNavigationViewModel.studentInfo.value = PackageData.loading()
		if (bottomNavigationViewModel.studentList.value == null || bottomNavigationViewModel.studentList.value?.data == null || bottomNavigationViewModel.studentList.value!!.data!!.isEmpty())
			bottomNavigationViewModel.courseList.value = PackageData.empty()
		else {
			StudentRemoteDataSource.queryStudentInfo(bottomNavigationViewModel.studentInfo, bottomNavigationViewModel.studentList.value!!.data!![0])
		}
	}

	fun queryStudentInfo(bottomNavigationViewModel: BottomNavigationViewModel, mainStudent: Student) {
		bottomNavigationViewModel.studentInfo.value = PackageData.loading()
		StudentRemoteDataSource.queryStudentInfo(bottomNavigationViewModel.studentInfo, mainStudent)
	}

	fun queryCurrentWeek(bottomNavigationViewModel: BottomNavigationViewModel) {
		bottomNavigationViewModel.currentWeek.value = PackageData.loading()
		if (bottomNavigationViewModel.startDateTime.value != null) {
			val week = CalendarUtil.getWeekFromCalendar(CalendarUtil.startDateTime)
			bottomNavigationViewModel.currentWeek.value = PackageData.content(week)
			bottomNavigationViewModel.week.value = week
		} else {
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
	}

	fun queryCacheCourses(bottomNavigationViewModel: BottomNavigationViewModel) {
		bottomNavigationViewModel.courseList.value = PackageData.loading()
		if (bottomNavigationViewModel.studentList.value == null || bottomNavigationViewModel.studentList.value?.data == null || bottomNavigationViewModel.studentList.value!!.data!!.isEmpty())
			bottomNavigationViewModel.courseList.value = PackageData.empty()
		else {
			bottomNavigationViewModel.todayCourseList.removeSource(bottomNavigationViewModel.courseList)
			bottomNavigationViewModel.todayCourseList.addSource(bottomNavigationViewModel.courseList) { packageData ->
				when (packageData.status) {
					Content -> {
						CourseUtil.getTodayCourse(packageData.data!!) {
							if (it.isEmpty())
								bottomNavigationViewModel.todayCourseList.value = PackageData.empty(it)
							else
								bottomNavigationViewModel.todayCourseList.value = PackageData.content(it)
						}
					}
					Loading -> bottomNavigationViewModel.todayCourseList.value = PackageData.loading()
					Empty -> bottomNavigationViewModel.todayCourseList.value = PackageData.empty()
					Error -> bottomNavigationViewModel.todayCourseList.value = PackageData.error(packageData.error)
				}
			}
			CourseLocalDataSource.queryCourseByUsername(bottomNavigationViewModel.courseList, bottomNavigationViewModel.studentList.value!!.data!![0], "2017-2018", "1", true)
		}
	}

	fun queryCacheCoursesForManyStudent(bottomNavigationViewModel: BottomNavigationViewModel) {
		bottomNavigationViewModel.courseList.value = PackageData.loading()
		if (bottomNavigationViewModel.studentList.value == null || bottomNavigationViewModel.studentList.value?.data == null || bottomNavigationViewModel.studentList.value!!.data!!.isEmpty())
			bottomNavigationViewModel.courseList.value = PackageData.empty()
		else {
			bottomNavigationViewModel.todayCourseList.removeSource(bottomNavigationViewModel.courseList)
			bottomNavigationViewModel.todayCourseList.addSource(bottomNavigationViewModel.courseList) { packageData ->
				when (packageData.status) {
					Content -> {
						CourseUtil.getTodayCourse(packageData.data!!) {
							if (it.isEmpty())
								bottomNavigationViewModel.todayCourseList.value = PackageData.empty(it)
							else
								bottomNavigationViewModel.todayCourseList.value = PackageData.content(it)
						}
					}
					Loading -> bottomNavigationViewModel.todayCourseList.value = PackageData.loading()
					Empty -> bottomNavigationViewModel.todayCourseList.value = PackageData.empty()
					Error -> bottomNavigationViewModel.todayCourseList.value = PackageData.error(packageData.error)
				}
			}
			CourseLocalDataSource.queryCourseWithManyStudent(bottomNavigationViewModel.courseList, bottomNavigationViewModel.studentList.value!!.data!!, "2017-2018", "1", true)
		}
	}

	fun queryCoursesOnline(bottomNavigationViewModel: BottomNavigationViewModel) {
		bottomNavigationViewModel.courseList.value = PackageData.loading()
		CourseRemoteDataSource.queryCourseByUsername(bottomNavigationViewModel.courseList, bottomNavigationViewModel.studentList.value!!.data!![0], "2017-2018", "1", false)
	}

	fun queryCoursesOnlineForManyStudent(bottomNavigationViewModel: BottomNavigationViewModel) {
		bottomNavigationViewModel.courseList.value = PackageData.loading()
		CourseRemoteDataSource.queryCourseWithManyStudent(bottomNavigationViewModel.courseList, bottomNavigationViewModel.studentList.value!!.data!!, "2017-2018", "1", false)
	}

	fun queryStudentList(bottomNavigationViewModel: BottomNavigationViewModel) {
		bottomNavigationViewModel.studentList.value = PackageData.loading()
		StudentLocalDataSource.queryAllStudentList(bottomNavigationViewModel.studentList)
	}

	fun queryNotice(bottomNavigationViewModel: BottomNavigationViewModel) {
		NoticeRepository.queryNoticeInMainActivity(bottomNavigationViewModel)
	}
}