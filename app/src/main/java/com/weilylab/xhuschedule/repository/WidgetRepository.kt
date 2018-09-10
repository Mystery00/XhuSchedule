package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.repository.local.CourseLocalDataSource
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.repository.local.TestLocalDataSource
import com.weilylab.xhuschedule.utils.CourseUtil
import com.weilylab.xhuschedule.utils.UserUtil
import com.weilylab.xhuschedule.viewModel.WidgetViewModelHelper
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.Status.*

object WidgetRepository {
	fun queryTodayCourse() {
		WidgetViewModelHelper.courseList.value = PackageData.loading()
		if (WidgetViewModelHelper.studentList.value == null || WidgetViewModelHelper.studentList.value?.data == null || WidgetViewModelHelper.studentList.value!!.data!!.isEmpty())
			WidgetViewModelHelper.courseList.value = PackageData.empty()
		else {
			WidgetViewModelHelper.todayCourseList.removeSource(WidgetViewModelHelper.courseList)
			WidgetViewModelHelper.todayCourseList.addSource(WidgetViewModelHelper.courseList) { packageData ->
				when (packageData.status) {
					Content -> {
						CourseUtil.getTodayCourse(packageData.data!!) {
							if (it.isEmpty())
								WidgetViewModelHelper.todayCourseList.value = PackageData.empty(it)
							else
								WidgetViewModelHelper.todayCourseList.value = PackageData.content(it)
						}
					}
					Loading -> WidgetViewModelHelper.todayCourseList.value = PackageData.loading()
					Empty -> WidgetViewModelHelper.todayCourseList.value = PackageData.empty()
					Error -> WidgetViewModelHelper.todayCourseList.value = PackageData.error(packageData.error)
				}
			}
			val mainStudent = UserUtil.findMainStudent(WidgetViewModelHelper.studentList.value!!.data)!!
			CourseLocalDataSource.queryCourseByUsername(WidgetViewModelHelper.courseList, mainStudent, null, null, true, true)
		}
	}

	fun queryTodayCourseForManyStudent() {
		WidgetViewModelHelper.courseList.value = PackageData.loading()
		if (WidgetViewModelHelper.studentList.value == null || WidgetViewModelHelper.studentList.value?.data == null || WidgetViewModelHelper.studentList.value!!.data!!.isEmpty())
			WidgetViewModelHelper.courseList.value = PackageData.empty()
		else {
			WidgetViewModelHelper.todayCourseList.removeSource(WidgetViewModelHelper.courseList)
			WidgetViewModelHelper.todayCourseList.addSource(WidgetViewModelHelper.courseList) { packageData ->
				when (packageData.status) {
					Content -> {
						CourseUtil.getTodayCourse(packageData.data!!) {
							if (it.isEmpty())
								WidgetViewModelHelper.todayCourseList.value = PackageData.empty(it)
							else
								WidgetViewModelHelper.todayCourseList.value = PackageData.content(it)
						}
					}
					Loading -> WidgetViewModelHelper.todayCourseList.value = PackageData.loading()
					Empty -> WidgetViewModelHelper.todayCourseList.value = PackageData.empty()
					Error -> WidgetViewModelHelper.todayCourseList.value = PackageData.error(packageData.error)
				}
			}
			CourseLocalDataSource.queryCourseWithManyStudent(WidgetViewModelHelper.courseList, WidgetViewModelHelper.studentList.value!!.data!!, null, null, true, true)
		}
	}

	fun queryTests() {
		WidgetViewModelHelper.testList.value = PackageData.loading()
		if (WidgetViewModelHelper.studentList.value == null || WidgetViewModelHelper.studentList.value?.data == null || WidgetViewModelHelper.studentList.value!!.data!!.isEmpty())
			WidgetViewModelHelper.testList.value = PackageData.empty()
		else {
			val mainStudent = UserUtil.findMainStudent(WidgetViewModelHelper.studentList.value!!.data)!!
			TestLocalDataSource.queryAllTestsByUsername(WidgetViewModelHelper.testList, mainStudent)
		}
	}

	fun queryTestsForManyStudent() {
		WidgetViewModelHelper.testList.value = PackageData.loading()
		if (WidgetViewModelHelper.studentList.value == null || WidgetViewModelHelper.studentList.value?.data == null || WidgetViewModelHelper.studentList.value!!.data!!.isEmpty())
			WidgetViewModelHelper.testList.value = PackageData.empty()
		else
			TestLocalDataSource.queryAllTestsForManyStudent(WidgetViewModelHelper.testList, WidgetViewModelHelper.studentList.value!!.data!!)
	}

	fun queryStudentList() {
		WidgetViewModelHelper.studentList.value = PackageData.loading()
		StudentLocalDataSource.queryAllStudentList(WidgetViewModelHelper.studentList)
	}
}