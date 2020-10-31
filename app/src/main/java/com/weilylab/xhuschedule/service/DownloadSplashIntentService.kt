/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.service

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.weilylab.xhuschedule.api.QiniuAPI
import com.weilylab.xhuschedule.constant.IntentConstant
import com.weilylab.xhuschedule.repository.DebugDataKeeper
import com.weilylab.xhuschedule.utils.getSplashImageFile
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import vip.mystery0.logs.Logs
import vip.mystery0.tools.utils.copyToFile
import vip.mystery0.tools.utils.md5

class DownloadSplashIntentService : JobIntentService() {
    companion object {
        private const val TAG = "DownloadSplashIntentService"
        private const val JOB_ID = 102

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, DownloadSplashIntentService::class.java, JOB_ID, work)
        }
    }

    private val qiniuAPI: QiniuAPI by inject()
    private val debugDataKeeper: DebugDataKeeper by inject()

    override fun onHandleWork(intent: Intent) {
        val qiniuPath = intent.getStringExtra(IntentConstant.INTENT_TAG_NAME_QINIU_PATH) ?: return
        val objectId = intent.getStringExtra(IntentConstant.INTENT_TAG_NAME_SPLASH_FILE_NAME)
                ?: return
        val file = getSplashImageFile(objectId) ?: return
        if (!file.parentFile!!.exists())
            file.parentFile!!.mkdirs()
        if (!file.exists())
            GlobalScope.launch(CoroutineExceptionHandler { _, throwable ->
                Logs.wtf(TAG, "download: ", throwable)
                debugDataKeeper.data["downloadSplashError"] = throwable.message
                        ?: "empty error message"
            }) {
                withContext(Dispatchers.IO) {
                    val body = qiniuAPI.download(qiniuPath)
                    body.byteStream().copyToFile(file)
                    file.md5()
                }
            }
    }
}
