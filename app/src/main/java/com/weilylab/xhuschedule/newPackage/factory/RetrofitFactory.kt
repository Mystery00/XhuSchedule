package com.weilylab.xhuschedule.newPackage.factory

import com.weilylab.xhuschedule.newPackage.interceptor.LoadCookiesInterceptor
import com.weilylab.xhuschedule.newPackage.interceptor.SaveCookiesInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

object RetrofitFactory {
	private val splashClient = OkHttpClient.Builder()
			.retryOnConnectionFailure(true)
			.connectTimeout(2, TimeUnit.SECONDS)
			.readTimeout(2, TimeUnit.SECONDS)
			.writeTimeout(2, TimeUnit.SECONDS)
			.addInterceptor(LoadCookiesInterceptor())
			.addInterceptor(SaveCookiesInterceptor())
			.build()

	private val client = OkHttpClient.Builder()
			.retryOnConnectionFailure(true)
			.connectTimeout(20, TimeUnit.SECONDS)
			.readTimeout(20, TimeUnit.SECONDS)
			.writeTimeout(20, TimeUnit.SECONDS)
			.addInterceptor(LoadCookiesInterceptor())
			.addInterceptor(SaveCookiesInterceptor())
			.addInterceptor(HttpLoggingInterceptor()
					.setLevel(HttpLoggingInterceptor.Level.BODY))
			.build()

	val tomcatRetrofit = Retrofit.Builder()
			.baseUrl("https://xhuschedule.mostpan.com")
			.client(client)
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.build()!!

	val phpRetrofit = Retrofit.Builder()
			.baseUrl("http://xhuschedule.mostpan.com:9783")
			.client(client)
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.build()!!

	val imgRetrofit = Retrofit.Builder()
			.baseUrl("http://download.xhuschedule.mostpan.com")
			.client(client)
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.build()!!

	val splashLeanCloudRetrofit = Retrofit.Builder()
			.baseUrl("https://f939ktgh.api.lncld.net")
			.client(splashClient)
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.build()!!

	val leanCloudRetrofit = Retrofit.Builder()
			.baseUrl("https://f939ktgh.api.lncld.net")
			.client(client)
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.build()!!
}