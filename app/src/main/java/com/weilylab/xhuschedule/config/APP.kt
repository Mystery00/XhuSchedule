/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.config

import android.annotation.SuppressLint
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.browser.customtabs.CustomTabsIntent
import androidx.multidex.MultiDexApplication
import com.oasisfeng.condom.CondomContext
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.tauth.Tencent
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.module.*
import com.weilylab.xhuschedule.utils.NotificationUtil
import com.weilylab.xhuschedule.utils.PackageUtil
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import vip.mystery0.crashhandler.CrashHandler
import vip.mystery0.tools.ToolsClient
import vip.mystery0.tools.utils.registerActivityLifecycle
import vip.mystery0.tools.utils.toastLong
import java.io.File

/**
 * Created by myste.
 */
class APP : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        instance = this
        startKoin {
            androidContext(this@APP)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                modules(
                    listOf(
                        appModule,
                        shortcutModule,
                        databaseModule,
                        networkModule,
                        repositoryModule,
                        viewModelModule
                    )
                )
            } else {
                modules(
                    listOf(
                        appModule,
                        databaseModule,
                        networkModule,
                        repositoryModule,
                        viewModelModule
                    )
                )
            }
        }
        val info = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        val debug = info.metaData.getBoolean("DEBUG")
        CrashHandler.config {
            setFileNameSuffix("log")
                .setDir(File(externalCacheDir, "crash"))
                .setAutoClean(true)
                .setDebug(debug)
        }.init()
        NotificationUtil.initChannelID(this)//初始化NotificationChannelID
        ToolsClient.initWithContext(this)
        registerActivityLifecycle()
        if (PackageUtil.isQQApplicationAvailable())
            tencent = try {
                Tencent.createInstance(
                    "1106663023",
                    CondomContext.wrap(applicationContext, "Tencent")
                )
            } catch (ignore: Exception) {
                Tencent.createInstance("1106663023", applicationContext)
            }
        if (PackageUtil.isWeiXinApplicationAvailable())
            wxAPI = try {
                WXAPIFactory.createWXAPI(
                    CondomContext.wrap(applicationContext, "WeiXin"),
                    "wx41799887957cbba8",
                    false
                )
            } catch (ignore: Exception) {
                WXAPIFactory.createWXAPI(applicationContext, "wx41799887957cbba8", false)
            }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set

        lateinit var instance: Application
            private set

        var tencent: Tencent? = null
            private set

        var wxAPI: IWXAPI? = null
            private set
    }
}

fun Context.toCustomTabs(url: String) {
    try {
        val builder = CustomTabsIntent.Builder()
        val intent = builder.build()
        intent.launchUrl(this, Uri.parse(url))
    } catch (e: Exception) {
        loadInBrowser(url)
    }
}

fun Context.loadInBrowser(url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        toastLong(R.string.hint_no_browser)
    }
}