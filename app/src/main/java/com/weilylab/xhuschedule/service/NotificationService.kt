/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.repository.NotificationRepository
import com.weilylab.xhuschedule.repository.StudentRepository
import com.weilylab.xhuschedule.ui.notification.TomorrowNotification
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import vip.mystery0.logs.Logs

class NotificationService : Service() {
	override fun onBind(intent: Intent?): IBinder? = null

	private val studentRepository: StudentRepository by inject()
	private val notificationRepository: NotificationRepository by inject()
	private val notificationManager: NotificationManager by inject()

	override fun onCreate() {
		super.onCreate()
		val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_DEFAULT)
				.setSmallIcon(R.mipmap.ic_stat_init)
				.setContentText(getString(R.string.hint_foreground_notification))
				.setAutoCancel(true)
				.setPriority(NotificationManagerCompat.IMPORTANCE_NONE)
				.build()
		startForeground(Constants.NOTIFICATION_ID_TOMORROW_INIT, notification)
		Logs.i("onStartJob: 任务执行了")

		GlobalScope.launch {
			val studentList = studentRepository.queryAllStudentList()
			val customThingList = notificationRepository.queryTomorrowCustomThing()
			withContext(Dispatchers.Main) {
				TomorrowNotification.notifyCustomThing(this@NotificationService, notificationManager, customThingList)
			}
			if (ConfigurationUtil.isEnableMultiUserMode) {
				val courseList = notificationRepository.queryTomorrowCourseForManyStudent(studentList)
				withContext(Dispatchers.Main) {
					TomorrowNotification.notifyCourse(this@NotificationService, notificationManager, courseList)
				}
				val testList = notificationRepository.queryTestsForManyStudent(studentList)
				val testColor = notificationRepository.generateColorList(testList)
				withContext(Dispatchers.Main) {
					TomorrowNotification.notifyTest(this@NotificationService, notificationManager, testList, testColor)
				}
			} else {
				val courseList = notificationRepository.queryTomorrowCourse(studentList)
				withContext(Dispatchers.Main) {
					TomorrowNotification.notifyCourse(this@NotificationService, notificationManager, courseList)
				}
				val testList = notificationRepository.queryTests(studentList)
				val testColor = notificationRepository.generateColorList(testList)
				withContext(Dispatchers.Main) {
					TomorrowNotification.notifyTest(this@NotificationService, notificationManager, testList, testColor)
				}
			}
			stopSelf()
		}
	}

	override fun onDestroy() {
		Logs.i("onDestroy: 任务结束")
		stopForeground(true)
		super.onDestroy()
	}
}