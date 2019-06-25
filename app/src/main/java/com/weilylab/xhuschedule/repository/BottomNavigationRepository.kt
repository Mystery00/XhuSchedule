package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.local.CourseLocalDataSource
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.repository.remote.CourseRemoteDataSource
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import com.weilylab.xhuschedule.utils.userDo.CourseUtil
import com.weilylab.xhuschedule.utils.userDo.UserUtil
import com.weilylab.xhuschedule.viewmodel.BottomNavigationViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.rx.OnlyCompleteObserver
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.Status.*

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

	/**
	 * 获取缓存的所有课程列表
	 */
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
			CustomThingRepository.getToday(bottomNavigationViewModel)
			CourseLocalDataSource.queryCourseByUsername(bottomNavigationViewModel.courseList, mainStudent, ConfigurationUtil.currentYear, ConfigurationUtil.currentTerm, isFromCache = true, isShowError = true)
		}
	}

	/**
	 * 获取缓存的所有课程的列表
	 * 多用户模式
	 */
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
			CustomThingRepository.getToday(bottomNavigationViewModel)
			CourseLocalDataSource.queryCourseWithManyStudent(bottomNavigationViewModel.courseList, bottomNavigationViewModel.studentList.value!!.data!!, ConfigurationUtil.currentYear, ConfigurationUtil.currentTerm, isFromCache = true, isShowError = true)
		}
	}

	fun queryCoursesOnline(bottomNavigationViewModel: BottomNavigationViewModel, isShowError: Boolean = true) {
		if (isShowError)
			bottomNavigationViewModel.courseList.value = PackageData.loading()
		if (bottomNavigationViewModel.studentList.value == null) {
			if (isShowError)
				bottomNavigationViewModel.courseList.value = PackageData.error(Exception(StringConstant.hint_null_student))
			return
		}
		val mainStudent = UserUtil.findMainStudent(bottomNavigationViewModel.studentList.value!!.data)
		if (mainStudent == null) {
			if (isShowError)
				bottomNavigationViewModel.courseList.value = PackageData.error(Exception(StringConstant.hint_null_student))
			return
		}
		CustomThingRepository.getToday(bottomNavigationViewModel)
		CourseRemoteDataSource.queryCourseByUsername(bottomNavigationViewModel.courseList, mainStudent, ConfigurationUtil.currentYear, ConfigurationUtil.currentTerm, false, isShowError)
	}

	fun queryCoursesOnlineForManyStudent(bottomNavigationViewModel: BottomNavigationViewModel, isShowError: Boolean = true) {
		if (isShowError)
			bottomNavigationViewModel.courseList.value = PackageData.loading()
		CustomThingRepository.getToday(bottomNavigationViewModel)
		CourseRemoteDataSource.queryCourseWithManyStudent(bottomNavigationViewModel.courseList, bottomNavigationViewModel.studentList.value!!.data!!, ConfigurationUtil.currentYear, ConfigurationUtil.currentTerm, false, isShowError)
	}

	fun queryStudentList(bottomNavigationViewModel: BottomNavigationViewModel) {
		bottomNavigationViewModel.studentList.value = PackageData.loading()
		StudentLocalDataSource.queryAllStudentList(bottomNavigationViewModel.studentList)
	}

	fun queryNotice(bottomNavigationViewModel: BottomNavigationViewModel, isFirst: Boolean) {
		if (!LayoutRefreshConfigUtil.isRefreshNoticeDone)
			return
		Observable.create<Boolean> {
			Thread.sleep(500)
			it.onComplete()
		}
				.subscribeOn(Schedulers.single())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<Boolean>() {
					override fun onFinish(data: Boolean?) {
						NoticeRepository.queryNoticeInMainActivity(bottomNavigationViewModel, isFirst)
					}

					override fun onError(e: Throwable) {
					}
				})
	}

	fun queryFeedBack(bottomNavigationViewModel: BottomNavigationViewModel) = FeedBackRepository.queryFeedBackMessageInMainActivity(bottomNavigationViewModel)
}