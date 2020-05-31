package com.weilylab.xhuschedule.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.constant.Constants
import vip.mystery0.logs.Logs

class NotificationService : Service() {
	override fun onBind(intent: Intent?): IBinder? = null

	@Suppress("UNCHECKED_CAST", "CheckResult")
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

//		Observable.create<Map<String, Any>> {
//			val studentList = StudentLocalDataSource.queryAllStudentListDo()
//			val customThingList = NotificationRepository.queryTomorrowCustomThing()
//			it.onNext(mapOf("customThing" to customThingList))
//			if (ConfigurationUtil.isEnableMultiUserMode) {
//				val courseList = NotificationRepository.queryTomorrowCourseForManyStudent(studentList)
//				it.onNext(mapOf("schedule" to courseList))
//				val testList = NotificationRepository.queryTestsForManyStudent(studentList)
//				val testColor = NotificationRepository.generateColorList(testList)
//				it.onNext(mapOf("test" to testList, "testColor" to testColor))
//			} else {
//				val courseList = NotificationRepository.queryTomorrowCourse(studentList)
//				it.onNext(mapOf("schedule" to courseList))
//				val testList = NotificationRepository.queryTests(studentList)
//				val testColor = NotificationRepository.generateColorList(testList)
//				it.onNext(mapOf("test" to testList, "testColor" to testColor))
//			}
//			it.onComplete()
//		}
//				.subscribeOn(Schedulers.io())
//				.observeOn(AndroidSchedulers.mainThread())
//				.subscribe(object : Observer<Map<String, Any>> {
//					override fun onComplete() {
//						stopSelf()
//					}
//
//					override fun onSubscribe(d: Disposable) {
//					}
//
//					override fun onNext(it: Map<String, Any>) {
//						when {
//							it.containsKey("customThing") -> {
//								TomorrowNotification.notifyCustomThing(this@NotificationService, it["customThing"] as List<CustomThing>)
//							}
//							it.containsKey("schedule") -> {
//								TomorrowNotification.notifyCourse(this@NotificationService, it["schedule"] as List<Schedule>)
//							}
//							it.containsKey("test") -> {
//								TomorrowNotification.notifyTest(this@NotificationService, it["test"] as List<Test>, it["testColor"] as IntArray)
//							}
//						}
//					}
//
//					override fun onError(e: Throwable) {
//						Logs.wtf("onError: ", e)
//						stopSelf()
//					}
//				})
	}

	override fun onDestroy() {
		Logs.i("onDestroy: 任务结束")
		stopForeground(true)
		super.onDestroy()
	}
}