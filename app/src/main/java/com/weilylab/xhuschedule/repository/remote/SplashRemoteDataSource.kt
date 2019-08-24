package com.weilylab.xhuschedule.repository.remote

import androidx.lifecycle.MediatorLiveData
import com.weilylab.xhuschedule.api.XhuScheduleCloudAPI
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.factory.fromJson
import com.weilylab.xhuschedule.model.Splash
import com.weilylab.xhuschedule.model.response.SplashResponse
import com.weilylab.xhuschedule.repository.ds.SplashDataSource
import com.weilylab.xhuschedule.repository.local.SplashLocalDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.rx.*
import java.util.concurrent.TimeUnit

object SplashRemoteDataSource : SplashDataSource {
	override fun requestSplash(splashPackageLiveData: MediatorLiveData<PackageData<Splash>>) {
		splashPackageLiveData.loading()
		RetrofitFactory.retrofit
				.create(XhuScheduleCloudAPI::class.java)
				.requestSplashInfo()
				.timeout(2, TimeUnit.SECONDS)
				.subscribeOn(Schedulers.io())
				.map {
					val splashResponse = it.fromJson<SplashResponse>()
					if (splashResponse.data == null || !splashResponse.data!!.enable)
						SplashLocalDataSource.removeSplash()
					else
						SplashLocalDataSource.saveSplash(splashResponse.data!!)
					splashResponse
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<SplashResponse>() {
					override fun onFinish(data: SplashResponse?) {
						if (data?.data == null) {
							SplashLocalDataSource.requestSplash(splashPackageLiveData)
						} else {
							splashPackageLiveData.content(data.data)
						}
					}

					override fun onError(e: Throwable) {
						splashPackageLiveData.error(e)
					}
				})
	}
}