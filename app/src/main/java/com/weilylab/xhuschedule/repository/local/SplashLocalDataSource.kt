package com.weilylab.xhuschedule.repository.local

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import com.weilylab.xhuschedule.config.APP
import com.weilylab.xhuschedule.constant.SharedPreferenceConstant
import com.weilylab.xhuschedule.model.Splash
import com.weilylab.xhuschedule.repository.ds.SplashDataSource
import vip.mystery0.rx.PackageData

object SplashLocalDataSource : SplashDataSource {
	override fun requestSplash(splashPackageLiveData: MediatorLiveData<PackageData<Splash>>) {
		splashPackageLiveData.value = PackageData.loading()
		val splash = getSplash()
		splashPackageLiveData.value = PackageData.content(splash)
	}

	private val sharedPreferences = APP.context.getSharedPreferences(SharedPreferenceConstant.FILE_NAME_SPLASH, Context.MODE_PRIVATE)

	fun saveSplash(splash: Splash) {
		sharedPreferences.edit()
				.putInt(SharedPreferenceConstant.FIELD_SPLASH_ID, splash.id)
				.putString(SharedPreferenceConstant.FIELD_SPLASH_URL, splash.splashUrl)
				.putString(SharedPreferenceConstant.FIELD_SPLASH_LOCATION_URL, splash.locationUrl)
				.putLong(SharedPreferenceConstant.FIELD_SPLASH_TIME, splash.splashTime)
				.putString(SharedPreferenceConstant.FIELD_SPLASH_IMAGE_MD5, splash.imageMD5)
				.apply()
	}

	fun removeSplash() {
		sharedPreferences.edit()
				.remove(SharedPreferenceConstant.FIELD_SPLASH_ID)
				.remove(SharedPreferenceConstant.FIELD_SPLASH_URL)
				.remove(SharedPreferenceConstant.FIELD_SPLASH_LOCATION_URL)
				.remove(SharedPreferenceConstant.FIELD_SPLASH_TIME)
				.remove(SharedPreferenceConstant.FIELD_SPLASH_IMAGE_MD5)
				.apply()
	}

	fun getSplash(): Splash {
		val splash = Splash()
		val isEnable = sharedPreferences.contains(SharedPreferenceConstant.FIELD_SPLASH_ID) &&
				sharedPreferences.contains(SharedPreferenceConstant.FIELD_SPLASH_URL) &&
				sharedPreferences.contains(SharedPreferenceConstant.FIELD_SPLASH_LOCATION_URL) &&
				sharedPreferences.contains(SharedPreferenceConstant.FIELD_SPLASH_TIME) &&
				sharedPreferences.contains(SharedPreferenceConstant.FIELD_SPLASH_IMAGE_MD5)
		splash.enable = isEnable
		if (isEnable) {
			splash.id = sharedPreferences.getInt(SharedPreferenceConstant.FIELD_SPLASH_ID, 0)
			splash.splashUrl = sharedPreferences.getString(SharedPreferenceConstant.FIELD_SPLASH_URL, "")!!
			splash.locationUrl = sharedPreferences.getString(SharedPreferenceConstant.FIELD_SPLASH_LOCATION_URL, "")!!
			splash.splashTime = sharedPreferences.getLong(SharedPreferenceConstant.FIELD_SPLASH_TIME, 0L)
			splash.imageMD5 = sharedPreferences.getString(SharedPreferenceConstant.FIELD_SPLASH_IMAGE_MD5, "")!!
		}
		return splash
	}
}