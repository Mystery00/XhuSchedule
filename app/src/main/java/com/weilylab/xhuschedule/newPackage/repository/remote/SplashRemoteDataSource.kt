package com.weilylab.xhuschedule.newPackage.repository.remote

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.api.LeanCloudAPI
import com.weilylab.xhuschedule.newPackage.factory.GsonFactory
import com.weilylab.xhuschedule.newPackage.factory.RetrofitFactory
import com.weilylab.xhuschedule.newPackage.model.response.SplashResponse
import com.weilylab.xhuschedule.newPackage.repository.SplashRepository
import com.weilylab.xhuschedule.newPackage.repository.dataSource.SplashDataSource
import com.weilylab.xhuschedule.newPackage.repository.local.SplashLocalDataSource
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs

object SplashRemoteDataSource : SplashDataSource {
	override fun requestSplash(splashLiveData: MutableLiveData<SplashResponse.Splash>, requestResultLiveData: MutableLiveData<Int>) {
		RetrofitFactory.leanCloudRetrofit
				.create(LeanCloudAPI::class.java)
				.requestSplashInfo()
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map {
					val splashResponse = GsonFactory.parseInputStream(it.byteStream(), SplashResponse::class.java)
					if (splashResponse.results.isNotEmpty())
						SplashLocalDataSource.saveSplash(splashResponse.results[0])
					splashResponse
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : RxObserver<SplashResponse>() {
					override fun onFinish(data: SplashResponse?) {
						if (data == null) {
							requestResultLiveData.value = SplashRepository.ERROR
							SplashLocalDataSource.requestSplash(splashLiveData, requestResultLiveData)
						} else {
							splashLiveData.value = data.results[0]
							requestResultLiveData.value = SplashRepository.DONE
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						SplashLocalDataSource.requestSplash(splashLiveData, requestResultLiveData)
					}
				})
	}
}