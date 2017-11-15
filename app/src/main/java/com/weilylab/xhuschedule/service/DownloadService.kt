package com.weilylab.xhuschedule.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.weilylab.xhuschedule.interfaces.UpdateResponse
import com.weilylab.xhuschedule.listener.DownloadProgressListener
import com.weilylab.xhuschedule.util.DownloadNotification
import com.weilylab.xhuschedule.util.FileUtil
import com.weilylab.xhuschedule.util.download.Download
import com.weilylab.xhuschedule.util.download.DownloadProgressInterceptor
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import vip.mystery0.tools.logs.Logs
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit


/**
 * Created by myste.
 */
class DownloadService : IntentService(TAG)
{

	companion object
	{
		private val TAG = "DownloadService"
		val APK = 1
		val PATCH = 2
	}

	private lateinit var retrofit: Retrofit

	override fun onHandleIntent(intent: Intent?)
	{
		Logs.i(TAG, "onHandleIntent: ")
		val type = when (intent?.getIntExtra("type", 0))
		{
			1 -> "apk"
			2 -> "patch"
			else ->
			{
				Logs.i(TAG, "onStartCommand: intent参数为空")
				return
			}
		}
		val fileName = intent.getStringExtra("fileName")
		val file = File(cacheDir.absolutePath + File.separator + type)
		if (!file.exists())
			file.mkdirs()
		Logs.i(TAG, "onStartCommand: type: " + type)
		Logs.i(TAG, "onStartCommand: fileName: " + fileName)
		Logs.i(TAG, "onStartCommand: " + file.absolutePath)
		download(this, type, fileName, file)
	}

	override fun onCreate()
	{
		Logs.i(TAG, "onCreate: ")
		super.onCreate()
		val listener = object : DownloadProgressListener
		{
			override fun update(bytesRead: Long, contentLength: Long, done: Boolean)
			{
				val download = Download()
				download.totalFileSize = contentLength
				download.currentFileSize = bytesRead
				download.progress = (bytesRead * 100 - contentLength).toInt()
				DownloadNotification.updateProgress(applicationContext, download)
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
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.build()
	}

	private fun download(context: Context, type: String, fileName: String, file: File)
	{
		retrofit.create(UpdateResponse::class.java)
				.download(type, fileName)
				.subscribeOn(Schedulers.io())
				.unsubscribeOn(Schedulers.io())
				.map({ responseBody -> responseBody.byteStream() })
				.observeOn(Schedulers.computation())
				.doOnNext { inputStream ->
					try
					{
						FileUtil.getInstance().saveFile(inputStream, file)
					}
					catch (e: IOException)
					{
						e.printStackTrace()
					}
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : Observer<InputStream>
				{
					override fun onSubscribe(d: Disposable)
					{
						DownloadNotification.notify(context)
					}

					override fun onComplete()
					{
						DownloadNotification.downloadDone(context)
					}

					override fun onNext(t: InputStream)
					{
					}

					override fun onError(e: Throwable)
					{
						DownloadNotification.downloadDone(context)
						e.printStackTrace()
					}
				})
	}

	override fun onTaskRemoved(rootIntent: Intent?)
	{
		DownloadNotification.cancel(this)
	}
}