package com.weilylab.xhuschedule.repository.remote

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.api.XhuScheduleCloudAPI
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.factory.fromJson
import com.weilylab.xhuschedule.model.response.StartDateTimeResponse
import com.weilylab.xhuschedule.repository.ds.InitDataSource
import com.weilylab.xhuschedule.repository.local.InitLocalDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.rx.OnlyCompleteObserver
import vip.mystery0.rx.PackageData
import vip.mystery0.tools.utils.NetworkTools
import java.util.*

object InitRemoteDataSource : InitDataSource {
	override fun getStartDateTime(startDateTimeLiveDate: MutableLiveData<PackageData<Calendar>>) {
		if (NetworkTools.instance.isConnectInternet())
			RetrofitFactory.retrofit
					.create(XhuScheduleCloudAPI::class.java)
					.requestStartDateTime()
					.subscribeOn(Schedulers.io())
					.map {
						val startDateTimeResponse = it.fromJson<StartDateTimeResponse>()
						if (startDateTimeResponse.code == 0)
							InitLocalDataSource.setStartDateTime(startDateTimeResponse.data)
						startDateTimeResponse
					}
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : OnlyCompleteObserver<StartDateTimeResponse>() {
						override fun onFinish(data: StartDateTimeResponse?) {
							if (data == null) {
								InitLocalDataSource.getStartDateTime(startDateTimeLiveDate)
							} else {
								val calendar = Calendar.getInstance()
								val dateArray = data.data.split('-')
								calendar.set(dateArray[0].toInt(), dateArray[1].toInt() - 1, dateArray[2].toInt(), 0, 0, 0)
								startDateTimeLiveDate.value = PackageData.content(calendar)
							}
						}

						override fun onError(e: Throwable) {
							InitLocalDataSource.getStartDateTime(startDateTimeLiveDate)
						}
					})
		else
			InitLocalDataSource.getStartDateTime(startDateTimeLiveDate)
	}
}