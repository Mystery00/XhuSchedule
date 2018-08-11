package com.weilylab.xhuschedule.newPackage.utils

import com.weilylab.xhuschedule.newPackage.api.TestAPI
import com.weilylab.xhuschedule.newPackage.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.newPackage.constant.StringConstant
import com.weilylab.xhuschedule.newPackage.factory.GsonFactory
import com.weilylab.xhuschedule.newPackage.factory.RetrofitFactory
import com.weilylab.xhuschedule.newPackage.listener.DoSaveListener
import com.weilylab.xhuschedule.newPackage.listener.RequestListener
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.Test
import com.weilylab.xhuschedule.newPackage.model.response.TestResponse
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs

object TestUtil {
	private const val RETRY_TIME = 1

	fun getTests(student: Student, doSaveListener: DoSaveListener<List<Test>>?, requestListener: RequestListener<List<Test>>, index: Int = 0) {
		RetrofitFactory.retrofit
				.create(TestAPI::class.java)
				.getTests(student.username)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map {
					val testResponse = GsonFactory.parseInputStream(it.byteStream(), TestResponse::class.java)
					if (testResponse.rt == ResponseCodeConstants.DONE)
						doSaveListener?.doSave(testResponse.tests)
					testResponse
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : RxObserver<TestResponse>() {
					override fun onFinish(data: TestResponse?) {
						when {
							data == null -> requestListener.error(ResponseCodeConstants.UNKNOWN_ERROR, StringConstant.hint_data_null)
							data.rt == ResponseCodeConstants.DONE -> requestListener.done(data.tests)
							data.rt == ResponseCodeConstants.ERROR_NOT_LOGIN -> {
								if (index == RETRY_TIME)
									requestListener.error(ResponseCodeConstants.DO_TOO_MANY, StringConstant.hint_do_too_many)
								else
									UserUtil.login(student, null, object : RequestListener<Boolean> {
										override fun done(t: Boolean) {
											getTests(student, doSaveListener, requestListener, index + 1)
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