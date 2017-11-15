package com.weilylab.xhuschedule.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.weilylab.xhuschedule.interfaces.UpdateResponse
import com.weilylab.xhuschedule.listener.DownloadProgressListener
import com.weilylab.xhuschedule.util.FileUtil
import com.weilylab.xhuschedule.util.download.Download
import com.weilylab.xhuschedule.util.download.DownloadProgressInterceptor
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.Subscriber
import vip.mystery0.tools.logs.Logs
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit
import rx.schedulers.Schedulers


/**
 * Created by myste.
 */
class DownloadService : Service()
{
	companion object
	{
		private val TAG = "DownloadService"
		val APK = 1
		val PATCH = 2
	}

	private lateinit var retrofit: Retrofit

	override fun onBind(intent: Intent): IBinder? = null

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

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
	{
		Logs.i(TAG, "onStartCommand: ")
		val type = when (intent?.getIntExtra("type", 0))
		{
			1 -> "apk"
			2 -> "patch"
			else ->
			{
				Logs.i(TAG, "onStartCommand: intent参数为空")
				return super.onStartCommand(intent, flags, startId)
			}
		}
		val fileName = intent.getStringExtra("fileName")
		val file = File(cacheDir.absolutePath + File.separator + type)
		if (!file.exists())
			file.mkdirs()
		Logs.i(TAG, "onStartCommand: type: " + type)
		Logs.i(TAG, "onStartCommand: fileName: " + fileName)
		Logs.i(TAG, "onStartCommand: " + file.absolutePath)
		return super.onStartCommand(intent, flags, startId)
	}

	private fun download(type: String, fileName: String, file: File)
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
				.subscribe(object : Subscriber<InputStream>()
				{
					override fun onCompleted()
					{
					}

					override fun onError(e: Throwable?)
					{
					}

					override fun onNext(t: InputStream?)
					{
					}
				})
	}
}