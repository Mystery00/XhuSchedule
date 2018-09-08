package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.local.CourseLocalDataSource
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.repository.remote.CourseRemoteDataSource
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.CourseUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import com.weilylab.xhuschedule.utils.UserUtil
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.rx.RxObservable
import vip.mystery0.rxpackagedata.rx.RxObserver
import com.weilylab.xhuschedule.viewModel.BottomNavigationViewModel
import vip.mystery0.rxpackagedata.Status.*

object BottomNavigationRepository {
	fun queryStudentInfo(bottomNavigationViewModel: BottomNavigationViewModel) {
		bottomNavigationViewModel.studentInfo.value = PackageData.loading()
		if (bottomNavigationViewModel.studentList.value == null || bottomNavigationViewModel.studentList.value?.data == null || bottomNavigationViewModel.studentList.value!!.data!!.isEmpty())
			bottomNavigationViewModel.courseList.value = PackageData.empty()
		else {
			val mainStudent = UserUtil.findMainStudent(bottomNavigationViewModel.studentList.value!!.data)!!
			StudentLocalDataSource.queryStudentInfo(bottomNavigationViewModel.studentInfo, mainStudent)
		}
	}

	fun queryStudentInfo(bottomNavigationViewModel: BottomNavigationViewModel, mainStudent: Student) {
		bottomNavigationViewModel.studentInfo.value = PackageData.loading()
		StudentLocalDataSource.queryStudentInfo(bottomNavigationViewModel.studentInfo, mainStudent)
	}

	fun queryCurrentWeek(bottomNavigationViewModel: BottomNavigationViewModel) {
		bottomNavigationViewModel.currentWeek.value = PackageData.loading()
		if (bottomNavigationViewModel.startDateTime.value != null && !LayoutRefreshConfigUtil.isRefreshStartTime) {
			val week = CalendarUtil.getWeekFromCalendar(CalendarUtil.startDateTime)
			bottomNavigationViewModel.currentWeek.value = PackageData.content(week)
			bottomNavigationViewModel.week.value = week
		} else {
			bottomNavigationViewModel.currentWeek.removeSource(bottomNavigationViewModel.startDateTime)
			bottomNavigationViewModel.currentWeek.addSource(bottomNavigationViewModel.startDateTime) {
				when (it.status) {
					Content -> if (it.data != null) {
						CalendarUtil.startDateTime = it.data!!
						val week = CalendarUtil.getWeekFromCalendar(it.data!!)
						bottomNavigationViewModel.currentWeek.value = PackageData.content(week)
						bottomNavigationViewModel.week.value = week
					}
					Loading -> bottomNavigationViewModel.currentWeek.value = PackageData.loading()
					Empty -> bottomNavigationViewModel.currentWeek.value = PackageData.empty()
					Error -> bottomNavigationViewModel.currentWeek.value = PackageData.error(it.error)
				}
			}
			InitRepository.getStartTime(bottomNavigationViewModel.startDateTime)
			LayoutRefreshConfigUtil.isRefreshStartTime = false
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
			val mainStudent = UserUtil.findMainStudent(bottomNavigationViewModel.studentList.value!!.data)!!
			CourseLocalDataSource.queryCourseByUsername(bottomNavigationViewModel.courseList, mainStudent, null, null, true)
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
			CourseLocalDataSource.queryCourseWithManyStudent(bottomNavigationViewModel.courseList, bottomNavigationViewModel.studentList.value!!.data!!, null, null, true)
		}
	}

	fun queryCoursesOnline(bottomNavigationViewModel: BottomNavigationViewModel) {
		bottomNavigationViewModel.courseList.value = PackageData.loading()
		val mainStudent = UserUtil.findMainStudent(bottomNavigationViewModel.studentList.value!!.data)!!
		CourseRemoteDataSource.queryCourseByUsername(bottomNavigationViewModel.courseList, mainStudent, null, null, false)
	}

	fun queryCoursesOnlineForManyStudent(bottomNavigationViewModel: BottomNavigationViewModel) {
		bottomNavigationViewModel.courseList.value = PackageData.loading()
		CourseRemoteDataSource.queryCourseWithManyStudent(bottomNavigationViewModel.courseList, bottomNavigationViewModel.studentList.value!!.data!!, null, null, false)
	}

	fun queryStudentList(bottomNavigationViewModel: BottomNavigationViewModel) {
		bottomNavigationViewModel.studentList.value = PackageData.loading()
		StudentLocalDataSource.queryAllStudentList(bottomNavigationViewModel.studentList)
	}

	fun queryNotice(bottomNavigationViewModel: BottomNavigationViewModel, isFirst: Boolean) {
		if (!LayoutRefreshConfigUtil.isRefreshNoticeDone)
			return
		RxObservable<Boolean>()
				.doThings {
					Thread.sleep(500)
					it.onFinish(true)
				}
				.subscribe(object : RxObserver<Boolean>() {
					override fun onFinish(data: Boolean?) {
						NoticeRepository.queryNoticeInMainActivity(bottomNavigationViewModel, isFirst)
					}

					override fun onError(e: Throwable) {
					}
				})
	}

	fun queryFeedBack(bottomNavigationViewModel: BottomNavigationViewModel) = FeedBackRepository.queryFeedBackMessageInMainActivity(bottomNavigationViewModel)
}