package com.weilylab.xhuschedule.repository.remote

import androidx.lifecycle.MediatorLiveData
import com.weilylab.xhuschedule.api.LeanCloudAPI
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.factory.fromJson
import com.weilylab.xhuschedule.model.response.SplashResponse
import com.weilylab.xhuschedule.repository.ds.SplashDataSource
import com.weilylab.xhuschedule.repository.local.SplashLocalDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.rx.OnlyCompleteObserver
import vip.mystery0.rx.PackageData
import java.util.concurrent.TimeUnit

object SplashRemoteDataSource : SplashDataSource {
	override fun requestSplash(splashPackageLiveData: MediatorLiveData<PackageData<SplashResponse.Splash>>) {
		splashPackageLiveData.value = PackageData.loading()
		RetrofitFactory.splashLeanCloudRetrofit
				.create(LeanCloudAPI::class.java)
				.requestSplashInfo()
				.timeout(2, TimeUnit.SECONDS)
				.subscribeOn(Schedulers.io())
				.map {
					val splashResponse = it.fromJson<SplashResponse>()
					if (splashResponse.results.isEmpty() || !splashResponse.results[0].isEnable)
						SplashLocalDataSource.removeSplash()
					else
						SplashLocalDataSource.saveSplash(splashResponse.results[0])
					splashResponse
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<SplashResponse>() {
					override fun onFinish(data: SplashResponse?) {
						if (data == null) {
							SplashLocalDataSource.requestSplash(splashPackageLiveData)
						} else {
							splashPackageLiveData.value = PackageData.content(data.results[0])
						}
					}

					override fun onError(e: Throwable) {
						splashPackageLiveData.value = PackageData.error(e)
					}
				})
	}
}