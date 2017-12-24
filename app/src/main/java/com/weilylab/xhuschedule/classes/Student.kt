/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-21 上午1:12
 */

package com.weilylab.xhuschedule.classes

import android.content.Context
import android.os.Build
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.rt.*
import com.weilylab.xhuschedule.interfaces.StudentService
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

    fun login(context: Context, listener: LoginListener) {
        login(false, context, listener)
    }

    private fun login(isTryLogin: Boolean, context: Context, listener: LoginListener) {
        val tag = "Student login"
        ScheduleHelper.tomcatRetrofit
                .create(StudentService::class.java)
                .autoLogin(username, password)
                .doOnComplete {
                    listener.doInThread()
                }
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map({ responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), LoginRT::class.java) })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<LoginRT> {
                    private var loginRT: LoginRT? = null
                    override fun onNext(t: LoginRT) {
                        Logs.i(tag, "onNext: ")
                        loginRT = t
                    }

                    override fun onComplete() {
                        Logs.i(tag, "onComplete: " + loginRT?.rt)
                        when (loginRT?.rt) {
                            "0" -> {
                                if (!isTryLogin)
                                    login(true, context, listener)
                                else
                                    listener.error(0, Exception(context.getString(R.string.error_timeout)))
                            }
                            "1" -> listener.loginDone(loginRT!!.name)
                            "2" -> listener.error(2, Exception(context.getString(R.string.error_invalid_username)))
                            "3" -> listener.error(3, Exception(context.getString(R.string.error_invalid_password)))
                            else -> listener.error(-2, Exception(context.getString(R.string.error_other)))
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

    fun getInfo(context: Context, listener: ProfileListener) {
        val tag = "Student getInfo"
        ScheduleHelper.tomcatRetrofit
                .create(StudentService::class.java)
                .getInfo(username)
                .doOnComplete {
                    listener.doInThread()
                }
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), StudentInfoRT::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<StudentInfoRT> {
                    private var studentInfoRT: StudentInfoRT? = null
                    override fun onNext(t: StudentInfoRT) {
                        Logs.i(tag, "onNext: ")
                        studentInfoRT = t
                    }

                    override fun onComplete() {
                        Logs.i(tag, "onComplete: " + studentInfoRT?.rt)
                        when (studentInfoRT?.rt) {
                            "0" -> listener.error(0, Exception(context.getString(R.string.error_timeout)))
                            "1" -> {
                                profile = Profile().map(studentInfoRT!!)
                                listener.got(profile!!)
                            }
                            "2" -> listener.error(2, Exception(context.getString(R.string.error_invalid_username)))
                            "3" -> listener.error(3, Exception(context.getString(R.string.error_invalid_password)))
                            "6" -> {
                                login(context, object : LoginListener {
                                    override fun doInThread() {
                                    }

                                    override fun loginDone(name: String) {
                                        getInfo(context, listener)
                                    }

                                    override fun error(rt: Int, e: Throwable) {
                                        listener.error(rt, e)
                                    }
                                })
                            }
                            else -> listener.error(-1, Exception(context.getString(R.string.error_other)))
                        }
                    }

                    override fun onError(e: Throwable) {
                        Logs.i(tag, "onError: ")
                        listener.error(-2, e)
                    }

                    override fun onSubscribe(d: Disposable) {
                        Logs.i(tag, "onSubscribe: ")
                    }
                })
    }

    fun getTests(context: Context, listener: GetArrayListener<Exam>) {
        val tag = "Student getTests"
        ScheduleHelper.tomcatRetrofit
                .create(StudentService::class.java)
                .getTests(username)
                .doOnComplete {
                    listener.doInThread()
                }
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), ExamRT::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ExamRT> {
                    private var examRT: ExamRT? = null
                    override fun onNext(t: ExamRT) {
                        Logs.i(tag, "onNext: ")
                        examRT = t
                    }

                    override fun onComplete() {
                        Logs.i(tag, "onComplete: " + examRT?.rt)
                        when (examRT?.rt) {
                            "0" -> listener.error(0, Exception(context.getString(R.string.error_timeout)))
                            "1" -> listener.got(examRT!!.tests)
                            "2" -> listener.error(2, Exception(context.getString(R.string.error_invalid_username)))
                            "3" -> listener.error(3, Exception(context.getString(R.string.error_invalid_password)))
                            "6" -> {
                                login(context, object : LoginListener {
                                    override fun doInThread() {
                                    }

                                    override fun loginDone(name: String) {
                                        getTests(context, listener)
                                    }

                                    override fun error(rt: Int, e: Throwable) {
                                        listener.error(rt, e)
                                    }
                                })
                            }
                            else -> listener.error(-1, Exception(context.getString(R.string.error_other)))
                        }
                    }

                    override fun onError(e: Throwable) {
                        Logs.i(tag, "onError: ")
                        listener.error(-2, e)
                    }

                    override fun onSubscribe(d: Disposable) {
                        Logs.i(tag, "onSubscribe: ")
                    }
                })
    }

    fun getScores(context: Context, year: String?, term: Int?, listener: GetScoreListener) {
        val tag = "Student getScores"
        ScheduleHelper.tomcatRetrofit
                .create(StudentService::class.java)
                .getScores(username, year, term)
                .doOnComplete {
                    listener.doInThread()
                }
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), ScoreRT::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ScoreRT> {
                    private var scoreRT: ScoreRT? = null
                    override fun onNext(t: ScoreRT) {
                        Logs.i(tag, "onNext: ")
                        scoreRT = t
                    }

                    override fun onComplete() {
                        Logs.i(tag, "onComplete: " + scoreRT?.rt)
                        when (scoreRT?.rt) {
                            "0" -> listener.error(0, Exception(context.getString(R.string.error_timeout)))
                            "1" -> listener.got(scoreRT!!.scores, scoreRT!!.failscores)
                            "2" -> listener.error(2, Exception(context.getString(R.string.error_invalid_username)))
                            "3" -> listener.error(3, Exception(context.getString(R.string.error_invalid_password)))
                            "6" -> {
                                login(context, object : LoginListener {
                                    override fun doInThread() {
                                    }

                                    override fun loginDone(name: String) {
                                        getScores(context, year, term, listener)
                                    }

                                    override fun error(rt: Int, e: Throwable) {
                                        listener.error(rt, e)
                                    }
                                })
                            }
                            else -> listener.error(-1, Exception(context.getString(R.string.error_other)))
                        }
                    }

                    override fun onError(e: Throwable) {
                        Logs.i(tag, "onError: ")
                        listener.error(-2, e)
                    }

                    override fun onSubscribe(d: Disposable) {
                        Logs.i(tag, "onSubscribe: ")
                    }
                })
    }

    fun feedback(context: Context, message: String, listener: FeedBackListener) {
        val tag = "Student feedback"
        ScheduleHelper.tomcatRetrofit.create(StudentService::class.java)
                .feedback(username,
                        context.getString(R.string.app_version_name) + "-" + context.getString(R.string.app_version_code),
                        Build.VERSION.RELEASE + "-" + Build.VERSION.SDK_INT,
                        "Build.MANUFACTURER",
                        "Build.MODEL",
                        "Build.DISPLAY",
//                        "Brand-${Build.BRAND}",
                        "other",
                        message)
                .doOnComplete {
                    listener.doInThread()
                }
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), FeedRT::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<FeedRT> {
                    private var feedRT: FeedRT? = null
                    override fun onNext(t: FeedRT) {
                        Logs.i(tag, "onNext: ")
                        feedRT = t
                    }

                    override fun onComplete() {
                        Logs.i(tag, "onComplete: " + feedRT?.rt)
                        when (feedRT?.rt) {
                            "0" -> listener.error(0, Exception(context.getString(R.string.error_timeout)))
                            "1" -> listener.done(1)
                            "2" -> listener.error(2, Exception(context.getString(R.string.error_invalid_username)))
                            "3" -> listener.error(3, Exception(context.getString(R.string.error_invalid_password)))
                            "6" -> {
                                login(context, object : LoginListener {
                                    override fun doInThread() {
                                    }

                                    override fun loginDone(name: String) {
                                        feedback(context, message, listener)
                                    }

                                    override fun error(rt: Int, e: Throwable) {
                                        listener.error(rt, e)
                                    }
                                })
                            }
                            else -> listener.error(-1, Exception(context.getString(R.string.error_other)))
                        }
                    }

                    override fun onError(e: Throwable) {
                        Logs.i(tag, "onError: ")
                        listener.error(-2, e)
                    }

                    override fun onSubscribe(d: Disposable) {
                        Logs.i(tag, "onSubscribe: ")
                    }
                })
    }
}