package com.weilylab.xhuschedule.service

import android.app.job.JobParameters
import android.app.job.JobService
import com.weilylab.xhuschedule.config.Status.*
import com.weilylab.xhuschedule.repository.NotificationRepository
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.ui.notification.TomorrowNotification
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import vip.mystery0.logs.Logs

class NotificationJobService : JobService() {
	private var isFinishCourse = false
	private var isFinishTest = false

	override fun onStopJob(params: JobParameters?): Boolean = ConfigurationUtil.notificationCourse || ConfigurationUtil.notificationExam

	override fun onStartJob(params: JobParameters?): Boolean {
		if (ConfigurationUtil.isEnableMultiUserMode)
			StudentLocalDataSource.queryAllStudentList { packageData ->
				when (packageData.status) {
					Content -> {
						if (ConfigurationUtil.notificationCourse)
							NotificationRepository.queryTomorrowCourseForManyStudent(packageData.data!!, null, null) {
								when (it.status) {
									Content -> {
										TomorrowNotification.notifyCourse(this, it.data!!)
										isFinishCourse = true
										checkFinish(params)
									}
									Empty, Error -> jobFinished(params, false)
								}
							}
						else
							isFinishCourse = true
						if (ConfigurationUtil.notificationExam)
							NotificationRepository.queryTomorrowTestForManyStudent(packageData.data!!) {
								when (it.status) {
									Content -> {
										TomorrowNotification.notifyTest(this, it.data!!)
										isFinishTest = true
										checkFinish(params)
									}
									Empty, Error -> jobFinished(params, false)
								}
							}
						else
							isFinishTest = true
					}
					Empty, Error -> jobFinished(params, false)
					else -> Logs.i("onStartJob: loading")
				}
			}
		else
			StudentLocalDataSource.queryMainStudent { packageData ->
				when (packageData.status) {
					Content -> {
						if (ConfigurationUtil.notificationCourse)
							NotificationRepository.queryTomorrowCourseByUsername(packageData.data!!, null, null) {
								when (it.status) {
									Content -> {
										TomorrowNotification.notifyCourse(this, it.data!!)
										isFinishCourse = true
										checkFinish(params)
									}
									Empty, Error -> jobFinished(params, false)
								}
							}
						else
							isFinishCourse = true
						if (ConfigurationUtil.notificationExam)
							NotificationRepository.queryTomorrowTestByUsername(packageData.data!!) {
								when (it.status) {
									Content -> {
										TomorrowNotification.notifyTest(this, it.data!!)
										isFinishTest = true
										checkFinish(params)
									}
									Empty, Error -> jobFinished(params, false)
								}
							}
						else
							isFinishTest = true
					}
					Empty, Error -> jobFinished(params, false)
					else -> Logs.i("onStartJob: loading")
				}
			}
		return true
	}

	private fun checkFinish(params: JobParameters?) {
		if (isFinishCourse && isFinishTest)
			jobFinished(params, false)
	}
}