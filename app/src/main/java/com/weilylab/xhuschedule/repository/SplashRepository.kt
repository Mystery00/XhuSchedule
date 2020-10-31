/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.repository

import android.content.Context
import androidx.core.content.edit
import com.weilylab.xhuschedule.api.XhuScheduleCloudAPI
import com.weilylab.xhuschedule.constant.SharedPreferenceConstant
import com.weilylab.xhuschedule.model.Splash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.tools.context
import vip.mystery0.tools.utils.isConnectInternet

class SplashRepository : KoinComponent {
    private val sharedPreferences = context().getSharedPreferences(SharedPreferenceConstant.FILE_NAME_SPLASH, Context.MODE_PRIVATE)

    private val xhuScheduleCloudAPI: XhuScheduleCloudAPI by inject()

    suspend fun requestSplash(): Splash {
        if (isConnectInternet()) {
            val splashResponse = xhuScheduleCloudAPI.requestSplashInfo()
            if (splashResponse.data == null || !splashResponse.data!!.enable)
                removeSplash()
            else
                saveSplash(splashResponse.data!!)
        }
        return getSplash()
    }

    suspend fun saveSplash(splash: Splash) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit {
                putInt(SharedPreferenceConstant.FIELD_SPLASH_ID, splash.id)
                        .putString(SharedPreferenceConstant.FIELD_SPLASH_URL, splash.splashUrl)
                        .putString(SharedPreferenceConstant.FIELD_SPLASH_LOCATION_URL, splash.locationUrl)
                        .putLong(SharedPreferenceConstant.FIELD_SPLASH_TIME, splash.splashTime)
                        .putString(SharedPreferenceConstant.FIELD_SPLASH_IMAGE_MD5, splash.imageMD5)
            }
        }
    }

    suspend fun removeSplash() {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit {
                remove(SharedPreferenceConstant.FIELD_SPLASH_ID)
                        .remove(SharedPreferenceConstant.FIELD_SPLASH_URL)
                        .remove(SharedPreferenceConstant.FIELD_SPLASH_LOCATION_URL)
                        .remove(SharedPreferenceConstant.FIELD_SPLASH_TIME)
                        .remove(SharedPreferenceConstant.FIELD_SPLASH_IMAGE_MD5)
            }
        }
    }

    suspend fun getSplash(): Splash {
        val splash = Splash()
        withContext(Dispatchers.IO) {
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
        }
        return splash
    }
}