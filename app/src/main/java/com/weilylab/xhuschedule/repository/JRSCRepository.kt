package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.api.JRSCAPI
import com.weilylab.xhuschedule.factory.GsonFactory
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.model.jrsc.JRSC
import com.weilylab.xhuschedule.model.jrsc.Token
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs
import vip.mystery0.rxpackagedata.rx.RxObserver

object JRSCRepository {
	fun load(listener: (String) -> Unit) {
		if (ConfigurationUtil.disableJRSC)
			return
		if (ConfigurationUtil.jrscToken == "") {
			RetrofitFactory.jrscRetrofit
					.create(JRSCAPI::class.java)
					.getToken()
					.subscribeOn(Schedulers.newThread())
					.map { GsonFactory.parse<Token>(it) }
					.unsubscribeOn(Schedulers.newThread())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : RxObserver<Token>() {
						override fun onError(e: Throwable) {
							Logs.wtf("onError: ", e)
						}

						override fun onFinish(data: Token?) {
							if (data != null && data.status == "success")
								ConfigurationUtil.jrscToken = data.token
						}
					})
		} else {
			RetrofitFactory.jrscRetrofit
					.create(JRSCAPI::class.java)
					.getJson()
					.subscribeOn(Schedulers.newThread())
					.map { GsonFactory.parse<JRSC>(it) }
					.unsubscribeOn(Schedulers.newThread())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : RxObserver<JRSC>() {
						override fun onError(e: Throwable) {
							Logs.wtf("onError: ", e)
						}

						override fun onFinish(data: JRSC?) {
							if (data != null && data.status == "success")
								listener.invoke(data.content.content)
						}
					})
		}
	}
}