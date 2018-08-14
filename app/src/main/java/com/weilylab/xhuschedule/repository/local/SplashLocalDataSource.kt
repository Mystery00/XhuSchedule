package com.weilylab.xhuschedule.repository.local

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import com.weilylab.xhuschedule.config.APP
import com.weilylab.xhuschedule.constant.SharedPreferenceConstant
import com.weilylab.xhuschedule.model.response.SplashResponse
import com.weilylab.xhuschedule.repository.dataSource.SplashDataSource
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData

object SplashLocalDataSource : SplashDataSource {
	override fun requestSplash(splashPackageLiveData: MediatorLiveData<PackageData<SplashResponse.Splash>>) {
		splashPackageLiveData.value = PackageData.loading()
		val splash = getSplash()
		splashPackageLiveData.value = PackageData.content(splash)
	}

	private val sharedPreferences = APP.context.getSharedPreferences(SharedPreferenceConstant.FILE_NAME_SPLASH, Context.MODE_PRIVATE)

	fun saveSplash(splash: SplashResponse.Splash) {
		sharedPreferences.edit()
				.putString(SharedPreferenceConstant.FIELD_SPLASH_ID, splash.objectId)
				.putString(SharedPreferenceConstant.FIELD_SPLASH_URL, splash.splashUrl)
				.putString(SharedPreferenceConstant.FIELD_SPLASH_LOCATION_URL, splash.locationUrl)
				.putLong(SharedPreferenceConstant.FIELD_SPLASH_TIME, splash.splashTime)
				.apply()
	}

	fun getSplash(): SplashResponse.Splash {
		val splash = SplashResponse.Splash()
		val isEnable = sharedPreferences.contains(SharedPreferenceConstant.FIELD_SPLASH_ID) &&
				sharedPreferences.contains(SharedPreferenceConstant.FIELD_SPLASH_URL) &&
				sharedPreferences.contains(SharedPreferenceConstant.FIELD_SPLASH_LOCATION_URL) &&
				sharedPreferences.contains(SharedPreferenceConstant.FIELD_SPLASH_TIME)
		splash.isEnable = isEnable
		if (isEnable) {
			splash.objectId = sharedPreferences.getString(SharedPreferenceConstant.FIELD_SPLASH_ID, "")!!
			splash.splashUrl = sharedPreferences.getString(SharedPreferenceConstant.FIELD_SPLASH_URL, "")!!
			splash.locationUrl = sharedPreferences.getString(SharedPreferenceConstant.FIELD_SPLASH_LOCATION_URL, "")!!
			splash.splashTime = sharedPreferences.getLong(SharedPreferenceConstant.FIELD_SPLASH_TIME, 0L)
		}
		return splash
	}
}