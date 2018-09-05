package com.weilylab.xhuschedule.factory

import com.weilylab.xhuschedule.interceptor.LoadCookiesInterceptor
import com.weilylab.xhuschedule.interceptor.SaveCookiesInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

object RetrofitFactory {
	private val splashClient = OkHttpClient.Builder()
			.retryOnConnectionFailure(true)
			.connectTimeout(1, TimeUnit.SECONDS)
			.readTimeout(1, TimeUnit.SECONDS)
			.writeTimeout(1, TimeUnit.SECONDS)
			.addInterceptor(LoadCookiesInterceptor())
			.addInterceptor(SaveCookiesInterceptor())
//			.addInterceptor(HttpLoggingInterceptor()
//					.setLevel(HttpLoggingInterceptor.Level.BODY))
			.build()

	private val client = OkHttpClient.Builder()
			.retryOnConnectionFailure(true)
			.connectTimeout(20, TimeUnit.SECONDS)
			.readTimeout(20, TimeUnit.SECONDS)
			.writeTimeout(20, TimeUnit.SECONDS)
			.addInterceptor(LoadCookiesInterceptor())
			.addInterceptor(SaveCookiesInterceptor())
//			.addInterceptor(HttpLoggingInterceptor()
//					.setLevel(HttpLoggingInterceptor.Level.BODY))
			.build()

	val retrofit = Retrofit.Builder()
			.baseUrl("https://xhuschedule.mostpan.com")
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