/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.activity

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import coil.load
import coil.request.CachePolicy
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.model.Splash
import com.weilylab.xhuschedule.viewmodel.SplashViewModel
import kotlinx.android.synthetic.main.activity_splash_image.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import vip.mystery0.logs.Logs
import vip.mystery0.rx.DataObserver
import vip.mystery0.tools.utils.screenHeight
import vip.mystery0.tools.utils.screenWidth
import java.io.File

@Suppress("DEPRECATION")
class SplashImageActivity : XhuBaseActivity(R.layout.activity_splash_image, false) {
    private val splashViewModel: SplashViewModel by viewModel()

    override fun inflateView(layoutId: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            super.inflateView(layoutId)
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            super.inflateView(layoutId)
        }
    }

    override fun initView() {
        super.initView()
        skipView.setUpdateTime(50)
                .setText(R.string.action_bypass)
                .setFinishAction {
                    gotoMain()
                }
    }

    override fun initData() {
        super.initData()
        splashViewModel.splashFile.observe(this, object : DataObserver<Pair<Splash, File>> {
            override fun empty() {
                super.empty()
                gotoMain()
            }

            override fun error(e: Throwable?) {
                super.error(e)
                Logs.w(e)
                empty()
            }

            override fun contentNoEmpty(data: Pair<Splash, File>) {
                imageView.load(data.second) {
                    size(screenWidth, screenHeight)
                    diskCachePolicy(CachePolicy.DISABLED)
                }
                imageView.setOnClickListener {
                    if (data.first.locationUrl != "")
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(data.first.locationUrl)))
                }
                skipView.setTotalTime(data.first.splashTime)
                        .start()
            }
        })
        splashViewModel.getSplash()
    }

    override fun monitor() {
        super.monitor()
        skipView.setOnClickListener {
            gotoMain()
        }
    }

    private fun gotoMain() {
        startActivity(Intent(this, BottomNavigationActivity::class.java))
        finish()
    }
}
