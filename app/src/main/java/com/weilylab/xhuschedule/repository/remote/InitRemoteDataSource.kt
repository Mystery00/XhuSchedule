package com.weilylab.xhuschedule.repository.remote

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.api.LeanCloudAPI
import com.weilylab.xhuschedule.factory.GsonFactory
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.model.response.StartDateTimeResponse
import com.weilylab.xhuschedule.repository.dataSource.InitDataSource
import com.weilylab.xhuschedule.repository.local.InitLocalDataSource
import com.weilylab.xhuschedule.utils.NetworkUtil
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.utils.rxAndroid.RxObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

object InitRemoteDataSource : InitDataSource {
	override fun getStartDateTime(startDateTimeLiveDate: MutableLiveData<PackageData<Calendar>>) {
		if (NetworkUtil.isConnectInternet())
			RetrofitFactory.leanCloudRetrofit
					.create(LeanCloudAPI::class.java)
					.requestStartDateTime()
					.subscribeOn(Schedulers.newThread())
					.unsubscribeOn(Schedulers.newThread())
					.map {
						val startDateTimeResponse = GsonFactory.parse<StartDateTimeResponse>(it)
						if (startDateTimeResponse.results.isNotEmpty())
							InitLocalDataSource.setStartDataTime(startDateTimeResponse.results[0].date)
						startDateTimeResponse
					}
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : RxObserver<StartDateTimeResponse>() {
						override fun onFinish(data: StartDateTimeResponse?) {
							if (data == null) {
								InitLocalDataSource.getStartDateTime(startDateTimeLiveDate)
							} else {
								val calendar = Calendar.getInstance()
								val dateArray = data.results[0].date.split('-')
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