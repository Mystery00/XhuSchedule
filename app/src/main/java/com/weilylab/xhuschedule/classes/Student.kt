/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-26 下午8:16
 */

package com.weilylab.xhuschedule.classes

import com.google.gson.Gson
import com.weilylab.xhuschedule.classes.rt.ExamRT
import com.weilylab.xhuschedule.classes.rt.LoginRT
import com.weilylab.xhuschedule.classes.rt.ScoreRT
import com.weilylab.xhuschedule.classes.rt.StudentInfoRT
import com.weilylab.xhuschedule.interfaces.UserService
import com.weilylab.xhuschedule.util.ScheduleHelper
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.logs.Logs
import java.io.InputStreamReader
import java.io.Serializable
import java.util.*

/**
 * Created by myste.
 */
class Student : Serializable {
    lateinit var username: String
    lateinit var password: String
    lateinit var name: String
    var profile: Profile? = null
    var todayCourses = ArrayList<Course>()
    var weekCourses = LinkedList<LinkedList<Course>>()
    var isReady = false

    fun login(): Observable<LoginRT> {
        return ScheduleHelper.tomcatRetrofit
                .create(UserService::class.java)
                .autoLogin(username, password)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map({ responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), LoginRT::class.java) })
    }

    fun getInfo(): Observable<StudentInfoRT> {
        return ScheduleHelper.tomcatRetrofit
                .create(UserService::class.java)
                .getInfo(username)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), StudentInfoRT::class.java) }
                .doOnNext { studentInfoRT ->
                    profile = Profile().map(studentInfoRT)
                }
    }

    fun getTests(): Observable<ExamRT> {
        return ScheduleHelper.tomcatRetrofit
                .create(UserService::class.java)
                .getTests(username)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), ExamRT::class.java) }
    }

    fun getScores(year: String?, term: Int?): Observable<ScoreRT> {
        return ScheduleHelper.tomcatRetrofit
                .create(UserService::class.java)
                .getScores(username, year, term)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), ScoreRT::class.java) }
    }

//    fun feedback():Observable<>
}