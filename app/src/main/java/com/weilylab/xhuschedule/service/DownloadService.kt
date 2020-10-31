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
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import androidx.core.app.JobIntentService
import androidx.core.content.FileProvider
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.api.QiniuAPI
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.constant.IntentConstant
import com.weilylab.xhuschedule.interceptor.DownloadProgressInterceptor
import com.weilylab.xhuschedule.listener.DownloadProgressListener
import com.weilylab.xhuschedule.model.Download
import com.weilylab.xhuschedule.ui.notification.DownloadNotification
import com.weilylab.xhuschedule.utils.BsPatch
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import vip.mystery0.logs.Logs
import vip.mystery0.tools.utils.copyToFile
import vip.mystery0.tools.utils.md5
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by myste.
 */
class DownloadService : JobIntentService() {
    companion object {
        private const val TAG = "DownloadService"
        private const val JOB_ID = 101

        fun intentTo(context: Context, type: String, qiniuPath: String, apkMD5: String, patchMD5: String) {
            val intent = Intent(context, DownloadService::class.java)
            intent.putExtra(IntentConstant.INTENT_TAG_NAME_TYPE, type)
            intent.putExtra(IntentConstant.INTENT_TAG_NAME_QINIU_PATH, qiniuPath)
            intent.putExtra(IntentConstant.INTENT_TAG_NAME_APK_MD5, apkMD5)
            intent.putExtra(IntentConstant.INTENT_TAG_NAME_PATCH_MD5, patchMD5)
            enqueueWork(context, intent)
        }

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, DownloadService::class.java, JOB_ID, work)
        }
    }

    private lateinit var retrofit: Retrofit
    private var isDownloadMD5Matched = false

    override fun onHandleWork(intent: Intent) {
        val type = intent.getStringExtra(IntentConstant.INTENT_TAG_NAME_TYPE)
        val qiniuPath = intent.getStringExtra(IntentConstant.INTENT_TAG_NAME_QINIU_PATH)
        val apkMD5 = intent.getStringExtra(IntentConstant.INTENT_TAG_NAME_APK_MD5)
        val patchMD5 = intent.getStringExtra(IntentConstant.INTENT_TAG_NAME_PATCH_MD5)
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath + File.separator + qiniuPath)
        if (!file.parentFile!!.exists())
            file.parentFile!!.mkdirs()
        if (TextUtils.isEmpty(type) || TextUtils.isEmpty(qiniuPath)) {
            Logs.i("onHandleIntent: 格式错误")
            return
        }
        download(this, type!!, qiniuPath!!, file, apkMD5!!, patchMD5!!)
    }

    override fun onCreate() {
        super.onCreate()

        val listener = object : DownloadProgressListener {
            override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
                val download = Download()
                download.totalFileSize = contentLength
                download.currentFileSize = bytesRead
                download.progress = (bytesRead * 100 / contentLength).toInt()
                DownloadNotification.updateProgress(applicationContext, download)
            }
        }
        val client = OkHttpClient.Builder()
                .addInterceptor(DownloadProgressInterceptor(listener))
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build()
        retrofit = Retrofit.Builder()
                .baseUrl("https://download.xhuschedule.mostpan.com")
                .client(client)
                .build()
    }

    private fun download(context: Context, type: String, qiniuPath: String, file: File, apkMD5: String, patchMD5: String) {
        GlobalScope.launch(CoroutineExceptionHandler { _, throwable ->
            Logs.wtf(TAG, "download: ", throwable)
            DownloadNotification.downloadError(context)
        }) {
            DownloadNotification.notify(context, qiniuPath)
            val downloadFileMD5 = withContext(Dispatchers.IO) {
                val body = retrofit.create(QiniuAPI::class.java).download(qiniuPath)
                body.byteStream().copyToFile(file)
                file.md5()
            }
            DownloadNotification.downloadFileMD5Matching(context)
            isDownloadMD5Matched = when (type) {
                Constants.DOWNLOAD_TYPE_APK -> downloadFileMD5 == apkMD5
                Constants.DOWNLOAD_TYPE_PATCH -> downloadFileMD5 == patchMD5
                else -> false
            }
            withContext(Dispatchers.IO) {
                if (isDownloadMD5Matched && type == Constants.DOWNLOAD_TYPE_PATCH) {
                    val newApkPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath + File.separator + "apk" + File.separator + qiniuPath + ".apk"
                    val newAPK = File(newApkPath)
                    if (!newAPK.parentFile!!.exists())
                        newAPK.parentFile!!.mkdirs()
                    BsPatch.patch(applicationContext.applicationInfo.sourceDir,
                            newApkPath,
                            file.absolutePath)
                    newApkPath
                } else {
                    null
                }
            }
            withContext(Dispatchers.Main) {
                if (!isDownloadMD5Matched) {
                    DownloadNotification.downloadFileMD5NotMatch(context)
                    return@withContext
                }
                val installIntent = Intent(Intent.ACTION_VIEW)
                installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                val installFile = if (type == Constants.DOWNLOAD_TYPE_PATCH)
                    File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath + File.separator + "apk" + File.separator + qiniuPath + ".apk")
                else
                    file
                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    FileProvider.getUriForFile(context, getString(R.string.app_package_name), installFile)
                else
                    Uri.fromFile(installFile)
                installIntent.setDataAndType(uri, "application/vnd.android.package-archive")
                startActivity(installIntent)
                DownloadNotification.cancel(context)
                stopSelf()
            }
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        DownloadNotification.cancel(this)
    }
}