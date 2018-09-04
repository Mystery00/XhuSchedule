package com.weilylab.xhuschedule.utils

import com.weilylab.xhuschedule.api.FeedbackAPI
import com.weilylab.xhuschedule.api.UserAPI
import com.weilylab.xhuschedule.config.Status.*
import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.factory.GsonFactory
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.listener.DoSaveListener
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.model.response.FeedbackResponse
import com.weilylab.xhuschedule.model.response.LoginResponse
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.utils.rxAndroid.RxObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs

object UserUtil {
	private const val RETRY_TIME = 1

	fun login(student: Student, doSaveListener: DoSaveListener<Student>?, requestListener: RequestListener<Boolean>) {
		RetrofitFactory.retrofit
				.create(UserAPI::class.java)
				.autoLogin(student.username, student.password)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map {
					val data = GsonFactory.parse<LoginResponse>(it)
					if (data.rt == ResponseCodeConstants.DONE)
						doSaveListener?.doSave(student)
					data
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : RxObserver<LoginResponse>() {
					override fun onFinish(data: LoginResponse?) {
						when {
							data == null -> requestListener.error(ResponseCodeConstants.UNKNOWN_ERROR, StringConstant.hint_data_null)
							data.rt == ResponseCodeConstants.DONE -> requestListener.done(true)
							else -> requestListener.error(data.rt, data.msg)
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						requestListener.error(ResponseCodeConstants.CATCH_ERROR, e.message)
					}
				})
	}

	fun getInfo(student: Student, doSaveListener: DoSaveListener<StudentInfo>?, requestListener: RequestListener<StudentInfo>, index: Int = 0) {
		RetrofitFactory.retrofit
				.create(UserAPI::class.java)
				.getInfo(student.username)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map {
					val data = GsonFactory.parse<StudentInfo>(it)
					if (data.rt == ResponseCodeConstants.DONE)
						doSaveListener?.doSave(data)
					data
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : RxObserver<StudentInfo>() {
					override fun onFinish(data: StudentInfo?) {
						when {
							data == null -> requestListener.error(ResponseCodeConstants.UNKNOWN_ERROR, StringConstant.hint_data_null)
							data.rt == ResponseCodeConstants.DONE -> requestListener.done(data)
							data.rt == ResponseCodeConstants.ERROR_NOT_LOGIN -> {
								if (index == RETRY_TIME)
									requestListener.error(ResponseCodeConstants.DO_TOO_MANY, StringConstant.hint_do_too_many)
								else
									login(student, null, object : RequestListener<Boolean> {
										override fun done(t: Boolean) {
											getInfo(student, doSaveListener, requestListener, index + 1)
										}

										override fun error(rt: String, msg: String?) {
											requestListener.error(rt, msg)
										}
									})
							}
							else -> requestListener.error(data.rt, data.msg)
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						requestListener.error(ResponseCodeConstants.CATCH_ERROR, e.message)
					}
				})
	}

	fun feedback(student: Student, requestListener: RequestListener<Boolean>, appVersion: String, systemVersion: String, manufacturer: String, model: String, rom: String, other: String, message: String, index: Int = 0) {
		RetrofitFactory.retrofit
				.create(FeedbackAPI::class.java)
				.feedback(student.username, appVersion, systemVersion, manufacturer, model, rom, other, message)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map { GsonFactory.parse<FeedbackResponse>(it) }
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : RxObserver<FeedbackResponse>() {
					override fun onFinish(data: FeedbackResponse?) {
						when {
							data == null -> requestListener.error(ResponseCodeConstants.UNKNOWN_ERROR, StringConstant.hint_data_null)
							data.rt == ResponseCodeConstants.DONE -> requestListener.done(true)
							data.rt == ResponseCodeConstants.ERROR_NOT_LOGIN -> {
								if (index == RETRY_TIME)
									requestListener.error(ResponseCodeConstants.DO_TOO_MANY, StringConstant.hint_do_too_many)
								else
									login(student, null, object : RequestListener<Boolean> {
										override fun done(t: Boolean) {
											feedback(student, requestListener, appVersion, systemVersion, manufacturer, model, rom, other, message)
										}

										override fun error(rt: String, msg: String?) {
											requestListener.error(rt, msg)
										}
									})
							}
							else -> requestListener.error(data.rt, data.msg)
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						requestListener.error(ResponseCodeConstants.CATCH_ERROR, e.message)
					}
				})
	}

	fun findMainStudent(list: List<Student>?): Student? = list?.firstOrNull { it.isMain }

	fun checkStudentLogged(student: Student, listener: (Boolean) -> Unit) {
		StudentLocalDataSource.queryAllStudentList { packageData ->
			when (packageData.status) {
				Content -> {
					var isLogged = false
					packageData.data!!.forEach {
						if (it.username == student.username) {
							isLogged = true
							return@forEach
						}
					}
					listener.invoke(isLogged)
				}
				Empty -> listener.invoke(false)
				Error -> listener.invoke(false)
			}
		}
	}
}