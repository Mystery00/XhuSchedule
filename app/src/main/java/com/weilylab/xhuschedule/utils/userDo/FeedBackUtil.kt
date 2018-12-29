package com.weilylab.xhuschedule.utils.userDo

import com.weilylab.xhuschedule.api.FeedbackAPI
import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.factory.GsonFactory
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.listener.DoSaveListener
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.FeedBackMessage
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.response.GetFeedBackMessageResponse
import com.weilylab.xhuschedule.model.response.SendFeedBackMessageResponse
import com.weilylab.xhuschedule.utils.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs
import vip.mystery0.rxpackagedata.rx.RxObserver

object FeedBackUtil {
	private const val RETRY_TIME = 1

	fun sendFeedBackMessage(student: Student, feedBackToken: String, content: String, requestListener: RequestListener<Boolean>, index: Int = 0) {
		if (NetworkUtil.isConnectInternet()) {
			RetrofitFactory.feedbackRetrofit
					.create(FeedbackAPI::class.java)
					.sendFBMessage(student.username, feedBackToken, content)
					.subscribeOn(Schedulers.newThread())
					.unsubscribeOn(Schedulers.newThread())
					.map { GsonFactory.parse<SendFeedBackMessageResponse>(it) }
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : RxObserver<SendFeedBackMessageResponse>() {
						override fun onFinish(data: SendFeedBackMessageResponse?) {
							when {
								data == null -> requestListener.error(ResponseCodeConstants.UNKNOWN_ERROR, StringConstant.hint_data_null)
								data.rt == ResponseCodeConstants.DONE -> requestListener.done(true)
								data.rt == ResponseCodeConstants.SERVER_TOKEN_INVALID_ERROR -> {
									if (index == RETRY_TIME)
										requestListener.error(ResponseCodeConstants.DO_TOO_MANY, StringConstant.hint_do_too_many)
									else
										UserUtil.login(student, null, object : RequestListener<Boolean> {
											override fun done(t: Boolean) {
												sendFeedBackMessage(student, feedBackToken, content, requestListener, index + 1)
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
		} else {
			requestListener.error(ResponseCodeConstants.CATCH_ERROR, StringConstant.hint_network_error)
		}
	}

	fun getFeedBackMessage(student: Student, feedBackToken: String, lastId: Int, doSaveListener: DoSaveListener<List<FeedBackMessage>>, requestListener: RequestListener<List<FeedBackMessage>>, index: Int = 0) {
		if (NetworkUtil.isConnectInternet()) {
			RetrofitFactory.feedbackRetrofit
					.create(FeedbackAPI::class.java)
					.getFBMessage(student.username, feedBackToken, lastId)
					.subscribeOn(Schedulers.newThread())
					.unsubscribeOn(Schedulers.newThread())
					.map {
						val getFeedBackMessageResponse = GsonFactory.parse<GetFeedBackMessageResponse>(it)
						if (getFeedBackMessageResponse.rt == ResponseCodeConstants.DONE)
							doSaveListener.doSave(getFeedBackMessageResponse.fBMessages)
						getFeedBackMessageResponse
					}
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : RxObserver<GetFeedBackMessageResponse>() {
						override fun onError(e: Throwable) {
							Logs.wtf("onError: ", e)
							requestListener.error(ResponseCodeConstants.CATCH_ERROR, e.message)
						}

						override fun onFinish(data: GetFeedBackMessageResponse?) {
							when {
								data == null -> requestListener.error(ResponseCodeConstants.UNKNOWN_ERROR, StringConstant.hint_data_null)
								data.rt == ResponseCodeConstants.DONE -> requestListener.done(data.fBMessages)
								data.rt == ResponseCodeConstants.SERVER_TOKEN_INVALID_ERROR -> {
									if (index == RETRY_TIME)
										requestListener.error(ResponseCodeConstants.DO_TOO_MANY, StringConstant.hint_do_too_many)
									else
										UserUtil.login(student, null, object : RequestListener<Boolean> {
											override fun done(t: Boolean) {
												getFeedBackMessage(student, feedBackToken, lastId, doSaveListener, requestListener, index + 1)
											}

											override fun error(rt: String, msg: String?) {
												requestListener.error(rt, msg)
											}
										})
								}
								else -> requestListener.error(data.rt, data.msg)
							}
						}
					})
		} else {
			requestListener.error(ResponseCodeConstants.CATCH_ERROR, StringConstant.hint_network_error)
		}
	}
}