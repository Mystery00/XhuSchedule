/*
 * Created by Mystery0 on 18-2-26 下午4:07.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 18-2-26 下午4:07
 */

package com.weilylab.xhuschedule.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Base64
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.CourseNotificationWithID
import com.weilylab.xhuschedule.classes.baseClass.Exam
import com.weilylab.xhuschedule.classes.baseClass.ExamNotificationWithID
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.classes.rt.GetCourseRT
import com.weilylab.xhuschedule.listener.GetCourseListener
import com.weilylab.xhuschedule.util.*
import com.weilylab.xhuschedule.util.notification.TomorrowCourseNotification
import com.weilylab.xhuschedule.util.notification.TomorrowExamNotification
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ShowNotificationService : Service() {
	companion object {
		private const val TAG = "ShowNotificationService"
	}

	private var queue = 0

	override fun onCreate() {
		super.onCreate()
		val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_DEFAULT)
				.setSmallIcon(R.drawable.ic_stat_foreground)
				.setContentText(getString(R.string.hint_foreground_notification))
				.setAutoCancel(true)
				.build()
		startForeground(Constants.NOTIFICATION_ID_FOREGROUND_ALARM, notification)
		Thread(Runnable {
			var isDone = false
			val timer = Timer()
			timer.schedule(object : TimerTask() {
				override fun run() {
					CourseUtil.getCoursesFromServer(this@ShowNotificationService, null, null, object : GetCourseListener {
						override fun start() {
							Logs.i(TAG, "start: ")
						}

						override fun got(studentList: ArrayList<Student>, rtList: ArrayList<GetCourseRT>) {
							Logs.i(TAG, "got: ")
							isDone = true
							showNotification()
						}

						override fun error(rt: Int, e: Throwable) {
							Logs.e(TAG, "rt: $rt error: $e")
							isDone = true
							showNotification()
						}
					})
				}
			}, 0)
			Thread.sleep(5000)
			if (!isDone) {
				timer.cancel()
				showNotification()
			}
		}).start()
	}

	private fun showNotification() {
		Logs.i(TAG, "showNotification: ")
		if (Settings.isNotificationTomorrowEnable) {
			queue++
			Observable.create<CourseNotificationWithID> {
				val studentList = XhuFileUtil.getArrayFromFile(XhuFileUtil.getStudentListFile(this), Student::class.java)
				for (i in studentList.indices) {
					val student = studentList[i]
					val parentFile = XhuFileUtil.getCourseCacheParentFile(this)
					if (!parentFile.exists())
						parentFile.mkdirs()
					val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
					//判断是否有缓存
					val cacheResult = parentFile.listFiles().filter { it.name == base64Name }.size == 1
					if (!cacheResult)
						continue
					val oldFile = File(parentFile, base64Name)
					if (!oldFile.exists())
						continue
					val courses = CourseUtil.getCoursesFromFile(oldFile)
					if (courses.isEmpty())
						continue
					val showCourses = if (Settings.notificationTomorrowType == 0)//今天
						CourseUtil.getTodayCourses(courses)
					else//明天
						CourseUtil.getTomorrowCourses(courses)
					if (showCourses.isNotEmpty())
						it.onNext(CourseNotificationWithID(i + Constants.NOTIFICATION_ID_COURSE_START_INDEX, showCourses))
				}
				it.onComplete()
			}
					.subscribeOn(Schedulers.newThread())
					.unsubscribeOn(Schedulers.newThread())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : DisposableObserver<CourseNotificationWithID>() {
						override fun onComplete() {
							queue--
						}

						override fun onNext(courseNotificationWithID: CourseNotificationWithID) {
							TomorrowCourseNotification.notify(this@ShowNotificationService, courseNotificationWithID.id, courseNotificationWithID.courses)
						}

						override fun onError(e: Throwable) {
							Logs.wtf(TAG, "onError: ", e)
							queue--
						}
					})
		}

		if (Settings.isNotificationExamEnable) {
			queue++
			Observable.create<ExamNotificationWithID> {
				val studentList = XhuFileUtil.getArrayFromFile(XhuFileUtil.getStudentListFile(this), Student::class.java)
				for (i in studentList.indices) {
					val student = studentList[i]
					val parentFile = XhuFileUtil.getExamParentFile(this)
					if (!parentFile.exists())
						parentFile.mkdirs()
					val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
					//判断是否有缓存
					val cacheResult = parentFile.listFiles().filter { it.name == base64Name }.size == 1
					if (!cacheResult)
						continue
					val oldFile = File(parentFile, base64Name)
					if (!oldFile.exists())
						continue
					val exams = XhuFileUtil.getArrayListFromFile(oldFile, Exam::class.java)
					if (exams.isEmpty())
						continue
					val calendar = Calendar.getInstance()
					val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
					val today = simpleDateFormat.format(calendar.time)
					val showExams = ArrayList<Exam>()
					showExams.addAll(exams.filter { it.date == today })
					if (showExams.isNotEmpty())
						it.onNext(ExamNotificationWithID(i + Constants.NOTIFICATION_ID_EXAM_START_INDEX, showExams))
				}
				it.onComplete()
			}
					.subscribeOn(Schedulers.newThread())
					.unsubscribeOn(Schedulers.newThread())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : DisposableObserver<ExamNotificationWithID>() {
						override fun onComplete() {
							queue--
						}

						override fun onNext(examNotificationWithID: ExamNotificationWithID) {
							TomorrowExamNotification.notify(this@ShowNotificationService, examNotificationWithID.id, examNotificationWithID.exams)
						}

						override fun onError(e: Throwable) {
							Logs.wtf(TAG, "onError: ", e)
							queue--
						}
					})
		}

		Observable.create<Boolean> {
			while (true) {
				Thread.sleep(1000)
				if (queue <= 0) {
					it.onComplete()
					break
				}
			}
		}
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : DisposableObserver<Boolean>() {
					override fun onComplete() {
						ScheduleHelper.setTrigger(this@ShowNotificationService)
						stopSelf()
					}

					override fun onNext(t: Boolean) {
					}

					override fun onError(e: Throwable) {
						ScheduleHelper.setTrigger(this@ShowNotificationService)
						stopSelf()
					}
				})
	}

	override fun onBind(intent: Intent): IBinder? {
		return null
	}

	override fun onDestroy() {
		stopForeground(true)
		super.onDestroy()
	}
}
