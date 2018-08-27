package com.weilylab.xhuschedule.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.config.Status.*
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.repository.NotificationRepository
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.ui.notification.TomorrowNotification
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import vip.mystery0.logs.Logs

class NotificationService : Service() {
	override fun onBind(intent: Intent?): IBinder? = null

	private var isFinishCourse = false
	private var isFinishTest = false

	override fun onCreate() {
		super.onCreate()
		val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_DEFAULT)
				.setSmallIcon(R.drawable.ic_stat_init)
				.setContentText(getString(R.string.hint_foreground_notification))
				.setAutoCancel(true)
				.setPriority(NotificationManagerCompat.IMPORTANCE_NONE)
				.build()
		startForeground(Constants.NOTIFICATION_ID_TOMORROW_INIT, notification)
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
										checkFinish()
									}
									Empty, Error -> stopSelf()
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
										checkFinish()
									}
									Empty, Error -> stopSelf()
								}
							}
						else
							isFinishTest = true
					}
					Empty, Error -> stopSelf()
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
										checkFinish()
									}
									Empty, Error -> stopSelf()
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
										checkFinish()
									}
									Empty, Error -> stopSelf()
								}
							}
						else
							isFinishTest = true
					}
					Empty, Error -> stopSelf()
				}
			}
	}

	private fun checkFinish() {
		if (isFinishCourse && isFinishTest)
			stopSelf()
	}

	override fun onDestroy() {
		stopForeground(true)
		super.onDestroy()
	}
}