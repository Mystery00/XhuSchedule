package com.weilylab.xhuschedule.newPackage.repository.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.config.APP
import com.weilylab.xhuschedule.newPackage.constant.SharedPreferenceConstant
import com.weilylab.xhuschedule.newPackage.model.SplashResponse
import com.weilylab.xhuschedule.newPackage.repository.SplashRepository
import com.weilylab.xhuschedule.newPackage.repository.dataSource.SplashDataSource

object SplashLocalDataSource : SplashDataSource {
	private val sharedPreferences = APP.context.getSharedPreferences(SharedPreferenceConstant.FILE_NAME_SPLASH, Context.MODE_PRIVATE)
	override fun requestSplash(splashLiveData: MutableLiveData<SplashResponse.Splash>, requestResultLiveData: MutableLiveData<Int>) {
		val splash = SplashResponse.Splash()
		val isEnable = sharedPreferences.contains(SharedPreferenceConstant.FIELD_SPLASH_ID) &&
				sharedPreferences.contains(SharedPreferenceConstant.FIELD_SPLASH_URL) &&
				sharedPreferences.contains(SharedPreferenceConstant.FIELD_SPLASH_LOCATION_URL) &&
				sharedPreferences.contains(SharedPreferenceConstant.FIELD_SPLASH_TIME)
		splash.isEnable = isEnable
		if (isEnable) {
			splash.objectId = sharedPreferences.getString(SharedPreferenceConstant.FIELD_SPLASH_ID, "")
			splash.splashUrl = sharedPreferences.getString(SharedPreferenceConstant.FIELD_SPLASH_URL, "")
			splash.locationUrl = sharedPreferences.getString(SharedPreferenceConstant.FIELD_SPLASH_LOCATION_URL, "")
			splash.splashTime = sharedPreferences.getLong(SharedPreferenceConstant.FIELD_SPLASH_TIME, 0L)
		}
		splashLiveData.value = splash
		requestResultLiveData.value = SplashRepository.DONE
	}

	fun saveSplash(splash: SplashResponse.Splash) {
		sharedPreferences.edit()
				.putString(SharedPreferenceConstant.FIELD_SPLASH_ID, splash.objectId)
				.putString(SharedPreferenceConstant.FIELD_SPLASH_URL, splash.splashUrl)
				.putString(SharedPreferenceConstant.FIELD_SPLASH_LOCATION_URL, splash.locationUrl)
				.putLong(SharedPreferenceConstant.FIELD_SPLASH_TIME, splash.splashTime)
				.apply()
	}
}