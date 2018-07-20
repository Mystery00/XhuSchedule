package com.weilylab.xhuschedule.newPackage.utils

import com.weilylab.xhuschedule.newPackage.api.CourseAPI
import com.weilylab.xhuschedule.newPackage.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.newPackage.constant.StringConstant
import com.weilylab.xhuschedule.newPackage.factory.GsonFactory
import com.weilylab.xhuschedule.newPackage.factory.RetrofitFactory
import com.weilylab.xhuschedule.newPackage.listener.DoSaveListener
import com.weilylab.xhuschedule.newPackage.listener.RequestListener
import com.weilylab.xhuschedule.newPackage.model.Course
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.response.CourseResponse
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs

object CourseUtil {
	private const val RETRY_TIME = 1

	fun getCourse(student: Student, year: String?, term: String?, doSaveListener: DoSaveListener<List<Course>>?, requestListener: RequestListener<List<Course>>, index: Int = 0) {
		RetrofitFactory.tomcatRetrofit
				.create(CourseAPI::class.java)
				.getCourses(student.username, year, term)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map {
					val courseResponse = GsonFactory.parseInputStream(it.byteStream(), CourseResponse::class.java)
					if (courseResponse.rt == ResponseCodeConstants.DONE)
						doSaveListener?.doSave(courseResponse.courses)
					courseResponse
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : RxObserver<CourseResponse>() {
					override fun onFinish(data: CourseResponse?) {
						when {
							data == null -> requestListener.error(ResponseCodeConstants.UNKNOWN_ERROR, StringConstant.hint_data_null)
							data.rt == ResponseCodeConstants.DONE -> requestListener.done(data.courses)
							data.rt == ResponseCodeConstants.ERROR_NOT_LOGIN -> {
								if (index == RETRY_TIME)
									requestListener.error(ResponseCodeConstants.DO_TOO_MANY, StringConstant.hint_do_too_many)
								else
									UserUtil.login(student, null, object : RequestListener<Boolean> {
										override fun done(t: Boolean) {
											getCourse(student, year, term, doSaveListener, requestListener, index + 1)
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