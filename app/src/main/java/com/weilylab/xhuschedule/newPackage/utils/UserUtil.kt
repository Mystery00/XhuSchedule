package com.weilylab.xhuschedule.newPackage.utils

import com.weilylab.xhuschedule.newPackage.api.UserAPI
import com.weilylab.xhuschedule.newPackage.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.newPackage.constant.StringConstant
import com.weilylab.xhuschedule.newPackage.factory.GsonFactory
import com.weilylab.xhuschedule.newPackage.factory.RetrofitFactory
import com.weilylab.xhuschedule.newPackage.listener.DoSaveListener
import com.weilylab.xhuschedule.newPackage.listener.RequestListener
import com.weilylab.xhuschedule.newPackage.model.response.BaseResponse
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.StudentInfo
import com.weilylab.xhuschedule.newPackage.model.response.LoginResponse
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs

object UserUtil {
	private const val RETRY_TIME = 1

	fun login(student: Student, doSaveListener: DoSaveListener<Student>?, requestListener: RequestListener<Boolean>) {
		RetrofitFactory.tomcatRetrofit
				.create(UserAPI::class.java)
				.autoLogin(student.username, student.password)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map {
					val data = GsonFactory.parseInputStream(it.byteStream(), LoginResponse::class.java)
					if (data.rt == ResponseCodeConstants.DONE)
						doSaveListener?.doSave(student)
					data
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : RxObserver<BaseResponse>() {
					override fun onFinish(data: BaseResponse?) {
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
		RetrofitFactory.tomcatRetrofit
				.create(UserAPI::class.java)
				.getInfo(student.username)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map {
					val data = GsonFactory.parseInputStream(it.byteStream(), StudentInfo::class.java)
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
}