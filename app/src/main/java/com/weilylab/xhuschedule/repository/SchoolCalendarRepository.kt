package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.api.XhuScheduleCloudAPI
import com.weilylab.xhuschedule.factory.RetrofitFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs
import vip.mystery0.rx.OnlyCompleteObserver

object SchoolCalendarRepository {
	fun getUrl(listener: (String?) -> Unit) {
		RetrofitFactory.retrofit
				.create(XhuScheduleCloudAPI::class.java)
				.schoolCalendar()
				.subscribeOn(Schedulers.io())
				.map { it.string() }
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<String>() {
					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						listener.invoke(null)
					}

					override fun onFinish(data: String?) {
						listener.invoke(data)
					}
				})
	}
}