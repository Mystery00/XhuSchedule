package com.weilylab.xhuschedule.repository.remote

import androidx.lifecycle.MediatorLiveData
import com.weilylab.xhuschedule.api.LeanCloudAPI
import com.weilylab.xhuschedule.factory.GsonFactory
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.model.response.SplashResponse
import com.weilylab.xhuschedule.repository.dataSource.SplashDataSource
import com.weilylab.xhuschedule.repository.local.SplashLocalDataSource
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.utils.rxAndroid.RxObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object SplashRemoteDataSource : SplashDataSource {
	override fun requestSplash(splashPackageLiveData: MediatorLiveData<PackageData<SplashResponse.Splash>>) {
		splashPackageLiveData.value = PackageData.loading()
		RetrofitFactory.splashLeanCloudRetrofit
				.create(LeanCloudAPI::class.java)
				.requestSplashInfo()
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map {
					val splashResponse = GsonFactory.parse<SplashResponse>(it)
					if (splashResponse.results.isNotEmpty())
						SplashLocalDataSource.saveSplash(splashResponse.results[0])
					splashResponse
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : RxObserver<SplashResponse>() {
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