/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-20 下午8:23
 */

package com.weilylab.xhuschedule.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.content.FileProvider
import com.weilylab.xhuschedule.interfaces.CommonService
import com.weilylab.xhuschedule.listener.DownloadProgressListener
import com.weilylab.xhuschedule.util.BsPatch
import com.weilylab.xhuschedule.util.notification.DownloadNotification
import com.weilylab.xhuschedule.util.XhuFileUtil
import com.weilylab.xhuschedule.util.download.Download
import com.weilylab.xhuschedule.util.download.DownloadProgressInterceptor
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import vip.mystery0.tools.logs.Logs
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit


/**
 * Created by myste.
 */
class DownloadService : IntentService(TAG) {

    companion object {
        private val TAG = "DownloadService"
    }

    private lateinit var retrofit: Retrofit

    override fun onHandleIntent(intent: Intent?) {
        Logs.i(TAG, "onHandleIntent: ")
        val type = intent?.getStringExtra("type")
        val fileName = intent?.getStringExtra("fileName")
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).absolutePath + File.separator + type + File.separator + fileName)
        if (!file.parentFile.exists())
            file.parentFile.mkdirs()
        if (type == null || type == "" || fileName == null || fileName == "") {
            Logs.i(TAG, "onHandleIntent: 格式错误")
            return
        }
        Logs.i(TAG, "onStartCommand: type: " + type)
        Logs.i(TAG, "onStartCommand: fileName: " + fileName)
        Logs.i(TAG, "onStartCommand: " + file.absolutePath)
        download(this, type, fileName, file)
    }

    override fun onCreate() {
        Logs.i(TAG, "onCreate: ")
        super.onCreate()

        val listener = object : DownloadProgressListener {
            private var temp = 0

            override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
                val download = Download()
                download.totalFileSize = contentLength
                download.currentFileSize = bytesRead
                download.progress = (bytesRead * 100 / contentLength).toInt()
                if (temp % 3 == 0)
                    DownloadNotification.updateProgress(applicationContext, download)
                temp++
            }
        }
        val client = OkHttpClient.Builder()
                .addInterceptor(DownloadProgressInterceptor(listener))
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build()
        retrofit = Retrofit.Builder()
                .baseUrl("http://tomcat.weilylab.com:9783")
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    private fun download(context: Context, type: String, fileName: String, file: File) {
        Logs.i(TAG, "download: " + fileName)
        retrofit.create(CommonService::class.java)
                .download(type, fileName)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .map({ responseBody -> responseBody.byteStream() })
                .observeOn(Schedulers.io())
                .doOnNext { inputStream ->
                    try {
                        XhuFileUtil.saveFile(inputStream, file)
                        if (type == "patch") {
                            val applicationInfo = applicationContext.applicationInfo
                            Logs.i(TAG, "patchAPK: " + applicationInfo.sourceDir)
                            val newApkPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).absolutePath + File.separator + "apk" + File.separator + fileName + ".apk"
                            val newAPK = File(newApkPath)
                            if (!newAPK.parentFile.exists())
                                newAPK.parentFile.mkdirs()
                            BsPatch.patch(applicationInfo.sourceDir,
                                    newApkPath,
                                    file.absolutePath)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<InputStream> {
                    override fun onSubscribe(d: Disposable) {
                        Logs.i(TAG, "onSubscribe: ")
                        DownloadNotification.notify(context, fileName)
                    }

                    override fun onComplete() {
                        val installIntent = Intent(Intent.ACTION_VIEW)
                        installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        val installFile = if (type == "patch")
                            File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).absolutePath + File.separator + "apk" + File.separator + fileName + ".apk")
                        else
                            file
                        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            FileProvider.getUriForFile(context, "com.weilylab.xhuschedule", installFile)
                        else
                            Uri.fromFile(installFile)
                        Logs.i(TAG, "onComplete: " + installFile.absolutePath)
                        Logs.i(TAG, "onComplete: " + uri)
                        installIntent.setDataAndType(uri, "application/vnd.android.package-archive")
                        startActivity(installIntent)
                        DownloadNotification.cancel(context)
                        stopSelf()
                    }

                    override fun onNext(t: InputStream) {
                    }

                    override fun onError(e: Throwable) {
                        DownloadNotification.downloadError(context)
                        e.printStackTrace()
                    }
                })
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        DownloadNotification.cancel(this)
    }
}