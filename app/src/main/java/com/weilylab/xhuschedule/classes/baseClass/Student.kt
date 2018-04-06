/*
 * Created by Mystery0 on 18-2-21 下午9:12.
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
 * Last modified 18-2-21 下午9:11
 */

package com.weilylab.xhuschedule.classes.baseClass

import android.content.Context
import android.os.Build
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.rt.*
import com.weilylab.xhuschedule.interfaces.CommonService
import com.weilylab.xhuschedule.interfaces.StudentService
import com.weilylab.xhuschedule.interfaces.UserService
import com.weilylab.xhuschedule.listener.*
import com.weilylab.xhuschedule.util.ConstantsCode
import com.weilylab.xhuschedule.util.ScheduleHelper
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs
import java.io.InputStreamReader
import java.io.Serializable
import kotlin.collections.ArrayList
import android.graphics.BitmapFactory
import android.util.Base64
import com.weilylab.xhuschedule.util.Constants
import java.util.*

class Student : Serializable {
	lateinit var username: String
	lateinit var password: String
	var profile: Profile? = null
	var todayCourses = ArrayList<Course>()
	var weekCourses = ArrayList<ArrayList<ArrayList<Course>>>()
	var isMain = false
	var isReady = false

	fun login(listener: LoginListener) {
		val tag = "student_login"
		ScheduleHelper.tomcatRetrofit
				.create(UserService::class.java)
				.autoLogin(username, password)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map({ ScheduleHelper.gson.fromJson(InputStreamReader(it.byteStream()), AutoLoginRT::class.java) })
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
							ConstantsCode.DONE -> listener.loginDone()
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
		getInfo(0, listener)
	}

	fun getInfo(index: Int, listener: ProfileListener) {
		val tag = "student_get_info-$index"
		ScheduleHelper.tomcatRetrofit
				.create(UserService::class.java)
				.getInfo(username)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map { ScheduleHelper.gson.fromJson(InputStreamReader(it.byteStream()), GetInfoRT::class.java) }
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : Observer<GetInfoRT> {
					private var getInfoRT: GetInfoRT? = null
					override fun onNext(t: GetInfoRT) {
						Logs.i(tag, "onNext: ")
						getInfoRT = t
					}

					override fun onComplete() {
						Logs.i(tag, "onComplete: " + getInfoRT?.rt)
						if (index == Constants.RETRY_TIME) {
							listener.error(-2, Exception("频繁操作！"))
							return
						}
						when (getInfoRT?.rt) {
							ConstantsCode.DONE -> {
								profile = Profile().map(getInfoRT!!)
								listener.got(profile!!)
							}
							ConstantsCode.ERROR_NOT_LOGIN -> {
								Timer().schedule(object : TimerTask() {
									override fun run() {
									}
								}, Constants.RETRY_TIME_OUT)
								login(object : LoginListener {
									override fun loginDone() {
										getInfo(index + 1, listener)
									}

									override fun error(rt: Int, e: Throwable) {
										listener.error(rt, e)
									}
								})
							}
							else -> listener.error(getInfoRT!!.rt.toInt(), Exception(getInfoRT?.msg))
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
		getTests(0, listener)
	}

	private fun getTests(index: Int, listener: GetArrayListener<Exam>) {
		val tag = "student_get_tests-$index"
		ScheduleHelper.tomcatRetrofit
				.create(StudentService::class.java)
				.getTests(username)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map { ScheduleHelper.gson.fromJson(InputStreamReader(it.byteStream()), GetTestsRT::class.java) }
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : Observer<GetTestsRT> {
					private var getTestsRT: GetTestsRT? = null
					override fun onNext(t: GetTestsRT) {
						Logs.i(tag, "onNext: ")
						getTestsRT = t
					}

					override fun onComplete() {
						Logs.i(tag, "onComplete: " + getTestsRT?.rt)
						if (index == Constants.RETRY_TIME) {
							listener.error(-2, Exception("频繁操作！"))
							return
						}
						when (getTestsRT?.rt) {
							ConstantsCode.DONE -> listener.got(getTestsRT!!.tests)
							ConstantsCode.ERROR_NOT_LOGIN -> {
								Timer().schedule(object : TimerTask() {
									override fun run() {
									}
								}, Constants.RETRY_TIME_OUT)
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
		getScores(0, year, term, listener)
	}

	private fun getScores(index: Int, year: String?, term: Int?, listener: GetScoreListener) {
		val tag = "student_get_scores-$index"
		ScheduleHelper.tomcatRetrofit
				.create(StudentService::class.java)
				.getScores(username, year, term)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map { ScheduleHelper.gson.fromJson(InputStreamReader(it.byteStream()), GetScoresRT::class.java) }
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : Observer<GetScoresRT> {
					private var getScoresRT: GetScoresRT? = null
					override fun onNext(t: GetScoresRT) {
						Logs.i(tag, "onNext: ")
						getScoresRT = t
					}

					override fun onComplete() {
						Logs.i(tag, "onComplete: " + getScoresRT?.rt)
						if (index == Constants.RETRY_TIME) {
							listener.error(-2, Exception("频繁操作！"))
							return
						}
						when (getScoresRT?.rt) {
							ConstantsCode.DONE -> listener.got(getScoresRT!!.scores, getScoresRT!!.failscores)
							ConstantsCode.ERROR_NOT_LOGIN -> {
								Timer().schedule(object : TimerTask() {
									override fun run() {
									}
								}, Constants.RETRY_TIME_OUT)
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

	fun getExpScores(year: String?, term: Int?, listener: GetExpScoreListener) {
		getExpScores(0, year, term, listener)
	}

	private fun getExpScores(index: Int, year: String?, term: Int?, listener: GetExpScoreListener) {
		val tag = "student_get_exp_scores-$index"
		ScheduleHelper.tomcatRetrofit
				.create(StudentService::class.java)
				.getExpScores(username, year, term)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map { ScheduleHelper.gson.fromJson(InputStreamReader(it.byteStream()), GetExpScoresRT::class.java) }
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : Observer<GetExpScoresRT> {
					private var getExpScoresRT: GetExpScoresRT? = null
					override fun onNext(t: GetExpScoresRT) {
						Logs.i(tag, "onNext: ")
						getExpScoresRT = t
					}

					override fun onComplete() {
						Logs.i(tag, "onComplete: " + getExpScoresRT?.rt)
						if (index == Constants.RETRY_TIME) {
							listener.error(-2, Exception("频繁操作！"))
							return
						}
						when (getExpScoresRT?.rt) {
							ConstantsCode.DONE -> listener.got(getExpScoresRT!!.expscores)
							ConstantsCode.ERROR_NOT_LOGIN -> {
								Timer().schedule(object : TimerTask() {
									override fun run() {
										login(object : LoginListener {
											override fun loginDone() {
												getExpScores(year, term, listener)
											}

											override fun error(rt: Int, e: Throwable) {
												listener.error(rt, e)
											}
										})
									}
								}, Constants.RETRY_TIME_OUT)
							}
							else -> listener.error(getExpScoresRT!!.rt.toInt(), Exception(getExpScoresRT?.msg))
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
		feedback(0, context, emailAddress, message, listener)
	}

	private fun feedback(index: Int, context: Context, emailAddress: String, message: String, listener: FeedBackListener) {
		val tag = "student_feedback-$index"
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
				.map { ScheduleHelper.gson.fromJson(InputStreamReader(it.byteStream()), FeedbackRT::class.java) }
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : Observer<FeedbackRT> {
					private var feedbackRT: FeedbackRT? = null
					override fun onNext(t: FeedbackRT) {
						Logs.i(tag, "onNext: ")
						feedbackRT = t
					}

					override fun onComplete() {
						Logs.i(tag, "onComplete: " + feedbackRT?.rt)
						if (index == Constants.RETRY_TIME) {
							listener.error(-2, Exception("频繁操作！"))
							return
						}
						when (feedbackRT?.rt) {
							ConstantsCode.DONE -> listener.done(1)
							ConstantsCode.ERROR_NOT_LOGIN -> {
								Timer().schedule(object : TimerTask() {
									override fun run() {
										login(object : LoginListener {
											override fun loginDone() {
												feedback(context, message, emailAddress, listener)
											}

											override fun error(rt: Int, e: Throwable) {
												listener.error(rt, e)
											}
										})
									}
								}, Constants.RETRY_TIME_OUT)
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

	fun getCETVCode(id: String, listener: GetCETVCodeListener) {
		getCETVCode(0, id, listener)
	}

	private fun getCETVCode(index: Int, id: String, listener: GetCETVCodeListener) {
		val tag = "student_get_cet_vcode-$index"
		ScheduleHelper.tomcatRetrofit.create(StudentService::class.java)
				.getCETVCode(username, id, null)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map { ScheduleHelper.gson.fromJson(InputStreamReader(it.byteStream()), GetCETVCodeRT::class.java) }
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : Observer<GetCETVCodeRT> {
					private var getCETVCodeRT: GetCETVCodeRT? = null
					override fun onComplete() {
						Logs.i(tag, "onComplete: ${getCETVCodeRT?.rt}")
						if (index == Constants.RETRY_TIME) {
							listener.error(-2, Exception("频繁操作！"))
							return
						}
						when (getCETVCodeRT?.rt) {
							ConstantsCode.DONE -> {
								val bytes = Base64.decode(getCETVCodeRT!!.vcode.substring(getCETVCodeRT!!.vcode.indexOfFirst { it == ',' } + 1), Base64.DEFAULT)
								listener.got(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
							}
							ConstantsCode.ERROR_NOT_LOGIN -> {
								Timer().schedule(object : TimerTask() {
									override fun run() {
										login(object : LoginListener {
											override fun loginDone() {
												getCETVCode(id, listener)
											}

											override fun error(rt: Int, e: Throwable) {
												listener.error(rt, e)
											}
										})
									}
								}, Constants.RETRY_TIME_OUT)
							}
							else -> listener.error(getCETVCodeRT!!.rt.toInt(), Exception(getCETVCodeRT?.msg))
						}
					}

					override fun onSubscribe(d: Disposable) {
						Logs.i(tag, "onSubscribe: ")
					}

					override fun onNext(t: GetCETVCodeRT) {
						Logs.i(tag, "onNext: ")
						getCETVCodeRT = t
					}

					override fun onError(e: Throwable) {
						Logs.i(tag, "onError: ")
						listener.error(-1, e)
					}
				})
	}

	fun getCETScores(id: String, name: String, vcode: String, listener: GetCETScoresListener) {
		getCETScores(0, id, name, vcode, listener)
	}

	private fun getCETScores(index: Int, id: String, name: String, vcode: String, listener: GetCETScoresListener) {
		val tag = "student_get_cet_scores-$index"
		ScheduleHelper.tomcatRetrofit.create(StudentService::class.java)
				.getCETScores(username, id, name, vcode)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map { ScheduleHelper.gson.fromJson(InputStreamReader(it.byteStream()), GetCETScoresRT::class.java) }
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : Observer<GetCETScoresRT> {
					private var getCETScoresRT: GetCETScoresRT? = null
					override fun onComplete() {
						Logs.i(tag, "onComplete: ${getCETScoresRT?.rt}")
						if (index == Constants.RETRY_TIME) {
							listener.error(-2, Exception("频繁操作！"))
							return
						}
						when (getCETScoresRT?.rt) {
							ConstantsCode.DONE -> listener.got(getCETScoresRT!!.cetScore)
							ConstantsCode.ERROR_NOT_LOGIN -> {
								Timer().schedule(object : TimerTask() {
									override fun run() {
										login(object : LoginListener {
											override fun loginDone() {
												getCETScores(id, name, vcode, listener)
											}

											override fun error(rt: Int, e: Throwable) {
												listener.error(rt, e)
											}
										})
									}
								}, Constants.RETRY_TIME_OUT)
							}
							else -> listener.error(getCETScoresRT!!.rt.toInt(), Exception(getCETScoresRT?.msg))
						}
					}

					override fun onSubscribe(d: Disposable) {
						Logs.i(tag, "onSubscribe: ")
					}

					override fun onNext(t: GetCETScoresRT) {
						Logs.i(tag, "onNext: ")
						getCETScoresRT = t
					}

					override fun onError(e: Throwable) {
						Logs.i(tag, "onError: ")
						listener.error(-1, e)
					}
				})
	}
}