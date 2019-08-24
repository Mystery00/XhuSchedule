package com.weilylab.xhuschedule.utils.userDo

import com.weilylab.xhuschedule.api.NoticeAPI
import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.factory.fromJson
import com.weilylab.xhuschedule.listener.DoSaveListener
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.Notice
import com.weilylab.xhuschedule.model.response.NoticeResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs
import vip.mystery0.rx.OnlyCompleteObserver

object NoticeUtil {
	private const val RETRY_TIME = UserUtil.RETRY_TIME

	fun getNotice(platform: String?, doSaveListener: DoSaveListener<List<Notice>>, requestListener: RequestListener<List<Notice>>, index: Int = 0) {
		RetrofitFactory.retrofit
				.create(NoticeAPI::class.java)
				.getNotices(platform)
				.subscribeOn(Schedulers.io())
				.map { responseBody ->
					val noticeResponse = responseBody.fromJson<NoticeResponse>()
					if (noticeResponse.rt == ResponseCodeConstants.DONE)
						doSaveListener.doSave(noticeResponse.notices)
					noticeResponse
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<NoticeResponse>() {
					override fun onFinish(data: NoticeResponse?) {
						when {
							data == null -> requestListener.error(ResponseCodeConstants.UNKNOWN_ERROR, StringConstant.hint_data_null)
							data.rt == ResponseCodeConstants.DONE -> requestListener.done(data.notices)
							else -> {
								if (index >= RETRY_TIME)
									requestListener.error(ResponseCodeConstants.DO_TOO_MANY, StringConstant.hint_do_too_many)
								else
									requestListener.error(data.rt, data.msg)
							}
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						requestListener.error(ResponseCodeConstants.CATCH_ERROR, e.message)
					}
				})
	}
}