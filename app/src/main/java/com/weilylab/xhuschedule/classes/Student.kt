package com.weilylab.xhuschedule.classes

import com.google.gson.Gson
import com.weilylab.xhuschedule.interfaces.RTResponse
import com.weilylab.xhuschedule.util.ScheduleHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.InputStreamReader
import java.io.Serializable

/**
 * Created by myste.
 */
class Student : Serializable {
    lateinit var username: String
    lateinit var password: String
    lateinit var name: String
    var todayCourses = ArrayList<Course>()
    var weekCourses = ArrayList<Course?>()
    var allCourses = ArrayList<Course?>()
    var isReady = false

    fun login(): Observable<LoginRT> {
        return ScheduleHelper.tomcatRetrofit
                .create(RTResponse::class.java)
                .autoLogin(username, password)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map({ responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), LoginRT::class.java) })
    }

}