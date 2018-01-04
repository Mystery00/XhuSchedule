/*
 * Created by Mystery0 on 18-1-4 下午5:13.
 * Copyright (c) 2018. All Rights reserved.
 *
 * Last modified 18-1-4 下午5:02
 */

package com.weilylab.xhuschedule.classes.baseClass

import android.content.Context
import android.os.Build
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.rt.*
import com.weilylab.xhuschedule.interfaces.CommonService
import com.weilylab.xhuschedule.interfaces.StudentService
import com.weilylab.xhuschedule.interfaces.UserService
import com.weilylab.xhuschedule.listener.*
import com.weilylab.xhuschedule.util.ScheduleHelper
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.logs.Logs
import java.io.InputStreamReader
import java.io.Serializable
import kotlin.collections.ArrayList

class Student : Serializable {
    lateinit var username: String
    lateinit var password: String
    lateinit var name: String
    var profile: Profile? = null
    var todayCourses = ArrayList<Course>()
    var weekCourses = ArrayList<ArrayList<ArrayList<Course>>>()
    var isMain = false
    var isReady = false

    fun login(listener: LoginListener) {
        val tag = "Student login"
        ScheduleHelper.tomcatRetrofit
                .create(UserService::class.java)
                .autoLogin(username, password)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map({ responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), AutoLoginRT::class.java) })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<AutoLoginRT> {
                    private var autoLoginRT: AutoLoginRT? = null
                    override fun onNext(t: AutoLoginRT) {
                        Logs.i(tag, "onNext: ")
                        autoLoginRT = t
                    }

                    override fun onComplete() {
                        Logs.i(tag, "onComplete: " + autoLoginRT?.rt)
                        when (autoLoginRT?.rt) {
                            "0" -> listener.loginDone()
                            else -> listener.error(autoLoginRT!!.rt.toInt(), Exception(autoLoginRT?.msg))
                        }
                    }

                    override fun onError(e: Throwable) {
                        Logs.i(tag, "onError: ")
                        listener.error(-1, e)
                    }

                    override fun onSubscribe(d: Disposable) {
                        Logs.i(tag, "onSubscribe: ")
                    }
                })
    }

    fun getInfo(listener: ProfileListener) {
        val tag = "Student getInfo"
        ScheduleHelper.tomcatRetrofit
                .create(StudentService::class.java)
                .getInfo(username)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), InfoRT::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<InfoRT> {
                    private var infoRT: InfoRT? = null
                    override fun onNext(t: InfoRT) {
                        Logs.i(tag, "onNext: ")
                        infoRT = t
                    }

                    override fun onComplete() {
                        Logs.i(tag, "onComplete: " + infoRT?.rt)
                        when (infoRT?.rt) {
                            "0" -> {
                                profile = Profile().map(infoRT!!)
                                listener.got(profile!!)
                            }
                            "405" -> {
                                login(object : LoginListener {
                                    override fun loginDone() {
                                        getInfo(listener)
                                    }

                                    override fun error(rt: Int, e: Throwable) {
                                        listener.error(rt, e)
                                    }
                                })
                            }
                            else -> listener.error(infoRT!!.rt.toInt(), Exception(infoRT?.msg))
                        }
                    }

                    override fun onError(e: Throwable) {
                        Logs.i(tag, "onError: ")
                        listener.error(-1, e)
                    }

                    override fun onSubscribe(d: Disposable) {
                        Logs.i(tag, "onSubscribe: ")
                    }
                })
    }

    fun getTests(listener: GetArrayListener<Exam>) {
        val tag = "Student getTests"
        ScheduleHelper.tomcatRetrofit
                .create(StudentService::class.java)
                .getTests(username)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), GetTestsRT::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<GetTestsRT> {
                    private var getTestsRT: GetTestsRT? = null
                    override fun onNext(t: GetTestsRT) {
                        Logs.i(tag, "onNext: ")
                        getTestsRT = t
                    }

                    override fun onComplete() {
                        Logs.i(tag, "onComplete: " + getTestsRT?.rt)
                        when (getTestsRT?.rt) {
                            "0" -> listener.got(getTestsRT!!.tests)
                            "405" -> {
                                login(object : LoginListener {
                                    override fun loginDone() {
                                        getTests(listener)
                                    }

                                    override fun error(rt: Int, e: Throwable) {
                                        listener.error(rt, e)
                                    }
                                })
                            }
                            else -> listener.error(getTestsRT!!.rt.toInt(), Exception(getTestsRT?.msg))
                        }
                    }

                    override fun onError(e: Throwable) {
                        Logs.i(tag, "onError: ")
                        listener.error(-1, e)
                    }

                    override fun onSubscribe(d: Disposable) {
                        Logs.i(tag, "onSubscribe: ")
                    }
                })
    }

    fun getScores(year: String?, term: Int?, listener: GetScoreListener) {
        val tag = "Student getScores"
        ScheduleHelper.tomcatRetrofit
                .create(StudentService::class.java)
                .getScores(username, year, term)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), GetScoresRT::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<GetScoresRT> {
                    private var getScoresRT: GetScoresRT? = null
                    override fun onNext(t: GetScoresRT) {
                        Logs.i(tag, "onNext: ")
                        getScoresRT = t
                    }

                    override fun onComplete() {
                        Logs.i(tag, "onComplete: " + getScoresRT?.rt)
                        when (getScoresRT?.rt) {
                            "0" -> listener.got(getScoresRT!!.scores, getScoresRT!!.failscores)
                            "405" -> {
                                login(object : LoginListener {
                                    override fun loginDone() {
                                        getScores(year, term, listener)
                                    }

                                    override fun error(rt: Int, e: Throwable) {
                                        listener.error(rt, e)
                                    }
                                })
                            }
                            else -> listener.error(getScoresRT!!.rt.toInt(), Exception(getScoresRT?.msg))
                        }
                    }

                    override fun onError(e: Throwable) {
                        Logs.i(tag, "onError: ")
                        listener.error(-1, e)
                    }

                    override fun onSubscribe(d: Disposable) {
                        Logs.i(tag, "onSubscribe: ")
                    }
                })
    }

    fun feedback(context: Context, emailAddress: String, message: String, listener: FeedBackListener) {
        val tag = "Student feedback"
        ScheduleHelper.tomcatRetrofit.create(CommonService::class.java)
                .feedback(username,
                        context.getString(R.string.app_version_name) + "-" + context.getString(R.string.app_version_code),
                        Build.VERSION.RELEASE + "-" + Build.VERSION.SDK_INT,
                        Build.MANUFACTURER,
                        Build.MODEL,
                        Build.DISPLAY,
                        "联系方式：$emailAddress",
                        message)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), FeedbackRT::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<FeedbackRT> {
                    private var feedbackRT: FeedbackRT? = null
                    override fun onNext(t: FeedbackRT) {
                        Logs.i(tag, "onNext: ")
                        feedbackRT = t
                    }

                    override fun onComplete() {
                        Logs.i(tag, "onComplete: " + feedbackRT?.rt)
                        when (feedbackRT?.rt) {
                            "0" -> listener.done(1)
                            "405" -> {
                                login(object : LoginListener {
                                    override fun loginDone() {
                                        feedback(context, message, emailAddress, listener)
                                    }

                                    override fun error(rt: Int, e: Throwable) {
                                        listener.error(rt, e)
                                    }
                                })
                            }
                            else -> listener.error(feedbackRT!!.rt.toInt(), Exception(feedbackRT?.msg))
                        }
                    }

                    override fun onError(e: Throwable) {
                        Logs.i(tag, "onError: ")
                        listener.error(-1, e)
                    }

                    override fun onSubscribe(d: Disposable) {
                        Logs.i(tag, "onSubscribe: ")
                    }
                })
    }
}