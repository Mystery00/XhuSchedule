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
import android.util.Base64
import com.weilylab.xhuschedule.classes.baseClass.CourseNotificationWithID
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.util.CalendarUtil
import com.weilylab.xhuschedule.util.CourseUtil
import com.weilylab.xhuschedule.util.XhuFileUtil
import com.weilylab.xhuschedule.util.notification.TomorrowInfoNotification
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.logs.Logs
import java.io.File

class ShowNotificationService : Service() {
    private val TAG = "ShowNotificationService"

    override fun onCreate() {
        super.onCreate()
        Observable.create<CourseNotificationWithID> {
            val studentList = XhuFileUtil.getArrayFromFile(XhuFileUtil.getStudentListFile(this), Student::class.java)
            val weekIndex = CalendarUtil.getWeekIndex()//周数
            val dayIndex = CalendarUtil.getWeekIndex()//星期几
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
                val courses = XhuFileUtil.getCoursesFromFile(this, oldFile)
                if (courses.isEmpty())
                    continue
                val showCourses = CourseUtil.getTodayCourses(courses)
                if (showCourses.isNotEmpty())
                    it.onNext(CourseNotificationWithID(i, showCourses))
            }
            it.onComplete()
        }
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<CourseNotificationWithID>() {
                    override fun onComplete() {
                        Logs.i(TAG, "onComplete: ")
                        stopSelf()
                    }

                    override fun onNext(courseNotificationWithID: CourseNotificationWithID) {
                        TomorrowInfoNotification.notify(this@ShowNotificationService, courseNotificationWithID.id, courseNotificationWithID.courses)
                    }

                    override fun onError(e: Throwable) {
                        Logs.wtf(TAG, "onError: ", e)
                    }
                })
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
