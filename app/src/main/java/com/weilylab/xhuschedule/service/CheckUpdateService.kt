/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.weilylab.xhuschedule.BuildConfig
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.api.XhuScheduleCloudAPI
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.model.Version
import com.weilylab.xhuschedule.model.event.CheckUpdateEvent
import com.weilylab.xhuschedule.repository.DebugDataKeeper
import com.weilylab.xhuschedule.ui.activity.GuideActivity
import com.weilylab.xhuschedule.ui.activity.SplashActivity
import com.weilylab.xhuschedule.ui.activity.SplashImageActivity
import com.weilylab.xhuschedule.ui.fragment.settings.SettingsPreferenceFragment
import com.weilylab.xhuschedule.utils.ConfigUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import vip.mystery0.tools.utils.ActivityManagerTools.finishAllActivity
import vip.mystery0.tools.utils.currentActivity
import vip.mystery0.tools.utils.toFormatFileSize

class CheckUpdateService : Service() {
    companion object {
        const val CHECK_ACTION_BY_MANUAL = "check_action_by_manual"
    }

    private val xhuScheduleCloudAPI: XhuScheduleCloudAPI by inject()

    private val eventBus: EventBus by inject()

    private val debugDataKeeper: DebugDataKeeper by inject()

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_DEFAULT)
                .setSmallIcon(R.mipmap.ic_stat_init)
                .setContentText(getString(R.string.hint_foreground_notification))
                .setAutoCancel(true)
                .setPriority(NotificationManagerCompat.IMPORTANCE_NONE)
                .build()
        startForeground(Constants.NOTIFICATION_ID_CHECK_UPDATE, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null)
            return super.onStartCommand(intent, flags, startId)
        val appVersion = if (BuildConfig.DEBUG) "debug" else "${getString(R.string.app_version_name)}-${getString(R.string.app_version_code)}"
        val systemVersion = "Android ${Build.VERSION.RELEASE}-${Build.VERSION.SDK_INT}"
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val rom = Build.DISPLAY
        GlobalScope.launch {
            val response = xhuScheduleCloudAPI.checkVersion(appVersion, systemVersion, manufacturer, model, rom, ConfigUtil.getDeviceID())
            if (response.code == ResponseCodeConstants.DONE.toInt()) {
                debugDataKeeper.data["latestVersion"] = "${response.data.versionName}-${response.data.versionCode}"
                debugDataKeeper.data["apkPath"] = response.data.apkQiniuPath
                debugDataKeeper.data["patchPath"] = response.data.patchQiniuPath
                if (response.data.versionCode.toInt() > getString(R.string.app_version_code).toInt()) {
                    withContext(Dispatchers.Main) {
                        showUpdateDialog(response.data, intent.getBooleanExtra(CHECK_ACTION_BY_MANUAL, false), response.data.must == "1")
                    }
                }
            }
            eventBus.post(CheckUpdateEvent(SettingsPreferenceFragment.ACTION_CHECK_UPDATE_DONE))
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private suspend fun showUpdateDialog(version: Version, checkByManual: Boolean, isMust: Boolean) {
        if (!checkByManual) {
            val ignoreVersionList = ConfigurationUtil.ignoreUpdateVersion.split('!')
            if (ignoreVersionList.indexOf(version.versionCode) != -1)
                return
        }
        val show = withContext(Dispatchers.Default) {
            while (currentActivity() is SplashActivity || currentActivity() is GuideActivity || currentActivity() is SplashImageActivity)
                Thread.sleep(1000)
            when {
                //自动检查更新
                ConfigurationUtil.autoCheckUpdate -> true
                //没有开启自动更新但是是必须的更新
                !ConfigurationUtil.autoCheckUpdate && isMust -> true
                //手动检查更新
                checkByManual -> true
                //其他情况
                else -> false
            }
        }
        if (!show) return
        withContext(Dispatchers.Main) {
            val activity = currentActivity() ?: return@withContext
            val title = getString(R.string.dialog_update_title, getString(R.string.app_version_name), version.versionName)
            val text = getString(R.string.dialog_update_text, version.updateLog)
            val builder = MaterialAlertDialogBuilder(activity)
                    .setTitle(title)
                    .setMessage(text)
                    .setPositiveButton("${getString(R.string.action_download_apk)}(${version.apkSize.toLong().toFormatFileSize()})") { _, _ ->
                        DownloadService.intentTo(activity, Constants.DOWNLOAD_TYPE_APK, version.apkQiniuPath, version.apkMD5, version.patchMD5)
                    }
            if (version.lastVersionCode == getString(R.string.app_version_code))
                builder.setNegativeButton("${getString(R.string.action_download_patch)}(${version.patchSize.toLong().toFormatFileSize()})") { _, _ ->
                    DownloadService.intentTo(activity, Constants.DOWNLOAD_TYPE_PATCH, version.patchQiniuPath, version.apkMD5, version.patchMD5)
                }
            if (version.must == "1")
                builder.setOnCancelListener {
                    finishAllActivity()
                }
            else
                builder.setNeutralButton(R.string.action_download_cancel) { _, _ ->
                    ConfigurationUtil.ignoreUpdateVersion = "${version.versionCode}!${ConfigurationUtil.ignoreUpdateVersion}"
                }
            builder.show()
        }
    }
}
