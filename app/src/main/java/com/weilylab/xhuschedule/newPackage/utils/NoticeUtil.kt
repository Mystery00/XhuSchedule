package com.weilylab.xhuschedule.newPackage.utils

import com.weilylab.xhuschedule.newPackage.api.NoticeAPI
import com.weilylab.xhuschedule.newPackage.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.newPackage.constant.StringConstant
import com.weilylab.xhuschedule.newPackage.factory.GsonFactory
import com.weilylab.xhuschedule.newPackage.factory.RetrofitFactory
import com.weilylab.xhuschedule.newPackage.listener.DoSaveListener
import com.weilylab.xhuschedule.newPackage.listener.RequestListener
import com.weilylab.xhuschedule.newPackage.model.Notice
import com.weilylab.xhuschedule.newPackage.model.response.NoticeResponse
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs

object NoticeUtil {
	private const val RETRY_TIME = 1

	fun getNotice(platform: String?, doSaveListener: DoSaveListener<List<Notice>>, requestListener: RequestListener<List<Notice>>, index: Int = 0) {
		RetrofitFactory.tomcatRetrofit
				.create(NoticeAPI::class.java)
				.getNotices(platform)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map {
					val noticeResponse = GsonFactory.parseInputStream(it.byteStream(), NoticeResponse::class.java)
					if (noticeResponse.rt == ResponseCodeConstants.DONE)
						doSaveListener.doSave(noticeResponse.notices)
					noticeResponse
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : RxObserver<NoticeResponse>() {
					override fun onFinish(data: NoticeResponse?) {
						when {
							data == null -> requestListener.error(ResponseCodeConstants.UNKNOWN_ERROR, StringConstant.hint_data_null)
							data.rt == ResponseCodeConstants.DONE -> requestListener.done(data.notices)
							else -> {
								if (index == RETRY_TIME)
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