/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-26 下午8:16
 */

package com.weilylab.xhuschedule.classes

import android.content.Context
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
import java.util.*

class Student : Serializable {
    lateinit var username: String
    lateinit var password: String
    lateinit var name: String
    var profile: Profile? = null
    var todayCourses = ArrayList<Course>()
    var weekCourses = LinkedList<LinkedList<Course>>()
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
                        Logs.i(tag, "onComplete: "+loginRT?.rt)
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
        getInfo(false, context, listener)
    }

    private fun getInfo(isTryRefreshData: Boolean, context: Context, listener: ProfileListener) {
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
                        Logs.i(tag, "onComplete: "+studentInfoRT?.rt)
                        when (studentInfoRT?.rt) {
                            "0" ->
                                if (!isTryRefreshData)
                                    getInfo(true, context, listener)
                                else
                                    listener.error(0, Exception(context.getString(R.string.error_timeout)))
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
                                        getInfo(true, context, listener)
                                    }

                                    override fun error(rt: Int, e: Throwable) {
                                        listener.error(rt, e)
                                    }
                                })
                            }
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

    fun getTests(context: Context, listener: GetArrayListener<Exam>) {
        getTests(false, context, listener)
    }

    private fun getTests(isTryRefreshData: Boolean, context: Context, listener: GetArrayListener<Exam>) {
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
                        Logs.i(tag, "onComplete: "+examRT?.rt)
                        when (examRT?.rt) {
                            "0" ->
                                if (!isTryRefreshData)
                                    getTests(true, context, listener)
                                else
                                    listener.error(0, Exception(context.getString(R.string.error_timeout)))
                            "1" -> listener.got(examRT!!.tests)
                            "2" -> listener.error(2, Exception(context.getString(R.string.error_invalid_username)))
                            "3" -> listener.error(3, Exception(context.getString(R.string.error_invalid_password)))
                            "6" -> {
                                login(context, object : LoginListener {
                                    override fun doInThread() {
                                    }

                                    override fun loginDone(name: String) {
                                        getTests(true, context, listener)
                                    }

                                    override fun error(rt: Int, e: Throwable) {
                                        listener.error(rt, e)
                                    }
                                })
                            }
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

    fun getScores(context: Context, year: String?, term: Int?, listener: GetScoreListener) {
        getScores(false, context, year, term, listener)
    }

    private fun getScores(isTryRefreshData: Boolean, context: Context, year: String?, term: Int?, listener: GetScoreListener) {
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
                        Logs.i(tag, "onComplete: "+scoreRT?.rt)
                        when (scoreRT?.rt) {
                            "0" ->
                                if (!isTryRefreshData)
                                    getScores(true, context, year, term, listener)
                                else
                                    listener.error(0, Exception(context.getString(R.string.error_timeout)))
                            "1" -> listener.got(scoreRT!!.scores, scoreRT!!.failscores)
                            "2" -> listener.error(2, Exception(context.getString(R.string.error_invalid_username)))
                            "3" -> listener.error(3, Exception(context.getString(R.string.error_invalid_password)))
                            "6" -> {
                                login(context, object : LoginListener {
                                    override fun doInThread() {
                                    }

                                    override fun loginDone(name: String) {
                                        getScores(true, context, year, term, listener)
                                    }

                                    override fun error(rt: Int, e: Throwable) {
                                        listener.error(rt, e)
                                    }
                                })
                            }
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