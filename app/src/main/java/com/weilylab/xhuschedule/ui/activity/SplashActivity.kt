/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.activity

import android.app.AlarmManager
import android.content.Intent
import android.content.pm.ShortcutManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.config.APP
import com.weilylab.xhuschedule.constant.IntentConstant
import com.weilylab.xhuschedule.model.Splash
import com.weilylab.xhuschedule.repository.InitRepository
import com.weilylab.xhuschedule.service.CheckUpdateService
import com.weilylab.xhuschedule.service.DownloadSplashIntentService
import com.weilylab.xhuschedule.utils.ConfigUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.viewmodel.SplashViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import vip.mystery0.rx.DataObserver
import vip.mystery0.tools.utils.sha1

/**
 * Created by mystery0.
 */
class SplashActivity : XhuBaseActivity(null, false) {
    private val splashViewModel: SplashViewModel by viewModel()
    private val initRepository: InitRepository by inject()
    private val alarmManager: AlarmManager by inject()
    private val shortcutManager: ShortcutManager by inject()

    private val splashObserver = object : DataObserver<Pair<Splash, Boolean>> {
        override fun empty() {
            gotoMain()
        }

        override fun error(e: Throwable?) {
            Log.e(TAG, "error: ", e)
            empty()
        }

        override fun contentNoEmpty(data: Pair<Splash, Boolean>) {
            super.contentNoEmpty(data)
            if (data.second)
                gotoSplashImage()
            else {
                val intent = Intent(this@SplashActivity, DownloadSplashIntentService::class.java)
                intent.putExtra(IntentConstant.INTENT_TAG_NAME_QINIU_PATH, data.first.splashUrl)
                intent.putExtra(
                    IntentConstant.INTENT_TAG_NAME_SPLASH_FILE_NAME,
                    data.first.splashUrl.sha1()
                )
                DownloadSplashIntentService.enqueueWork(this@SplashActivity, intent)
                empty()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN)
        when (ConfigurationUtil.nightMode) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            3 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        super.initView()
        if (ConfigurationUtil.firstEnter) {
            startActivity(Intent(this, GuideActivity::class.java))
            finish()
            return
        }
    }

    override fun initData() {
        super.initData()
        initViewModel()
        splashViewModel.requestSplash()
        ContextCompat.startForegroundService(
            this,
            Intent(APP.context, CheckUpdateService::class.java)
        )
        ConfigUtil.setTrigger(this, alarmManager)
        launch {
            ConfigUtil.getCurrentYearAndTerm(initRepository.getStartTime())
        }
    }

    private fun initViewModel() {
        splashViewModel.splashData.observe(this, splashObserver)
    }

    private fun gotoMain() {
        startActivity(Intent(this, BottomNavigationActivity::class.java))
        finish()
    }

    private fun gotoSplashImage() {
        startActivity(Intent(this, SplashImageActivity::class.java))
        finish()
    }

    companion object {
        private const val TAG = "SplashActivity"
    }
}