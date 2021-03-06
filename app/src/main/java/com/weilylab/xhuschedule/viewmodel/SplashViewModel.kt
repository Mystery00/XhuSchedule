/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Splash
import com.weilylab.xhuschedule.repository.DebugDataKeeper
import com.weilylab.xhuschedule.repository.SplashRepository
import com.weilylab.xhuschedule.utils.getSplashImageFile
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.content
import vip.mystery0.rx.empty
import vip.mystery0.rx.launch
import vip.mystery0.tools.utils.md5
import vip.mystery0.tools.utils.sha1
import java.io.File

class SplashViewModel : ViewModel(), KoinComponent {
    private val splashRepository: SplashRepository by inject()
    private val debugDataKeeper: DebugDataKeeper by inject()
    val splashData by lazy { MediatorLiveData<PackageData<Pair<Splash, Boolean>>>() }
    val splashFile by lazy { MediatorLiveData<PackageData<Pair<Splash, File>>>() }

    fun requestSplash() {
        launch(splashData) {
            val splash = splashRepository.requestSplash()
            debugDataKeeper.data["splashUrl"] = splash.splashUrl
            debugDataKeeper.data["splashTime"] = splash.splashTime
            debugDataKeeper.data["splashLocationUrl"] = splash.locationUrl
            if (splash.enable) {
                val fileName = splash.splashUrl.sha1()
                val splashFile = getSplashImageFile(fileName)
                if (splashFile != null && splashFile.exists()) {
                    val md5 = splashFile.md5()
                    if (splash.imageMD5 == md5)
                        splashData.content(splash to true)
                    else {
                        splashFile.delete()
                        splashData.empty()
                    }
                } else {
                    splashData.content(splash to false)
                }
            } else
                splashData.empty()
        }
    }

    fun getSplash() {
        launch(splashFile) {
            val splash = splashRepository.getSplash()
            if (splash.enable) {
                val fileName = splash.splashUrl.sha1()
                val file = getSplashImageFile(fileName)
                if (file != null && file.exists()) {
                    splashFile.content(splash to file)
                } else {
                    splashFile.empty()
                }
            } else {
                splashFile.empty()
            }
        }
    }
}