package com.weilylab.xhuschedule.factory

import com.weilylab.xhuschedule.interceptor.DebugInterceptor
import com.weilylab.xhuschedule.interceptor.LoadCookiesInterceptor
import com.weilylab.xhuschedule.interceptor.SaveCookiesInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

object RetrofitFactory {
	private val splashClient by lazy {
		OkHttpClient.Builder()
				.retryOnConnectionFailure(true)
				.connectTimeout(1, TimeUnit.SECONDS)
				.readTimeout(1, TimeUnit.SECONDS)
				.writeTimeout(1, TimeUnit.SECONDS)
				.addInterceptor(LoadCookiesInterceptor())
				.addInterceptor(SaveCookiesInterceptor())
				.addInterceptor(DebugInterceptor())
				.build()
	}

	private val client by lazy {
		OkHttpClient.Builder()
				.retryOnConnectionFailure(true)
				.connectTimeout(20, TimeUnit.SECONDS)
				.readTimeout(20, TimeUnit.SECONDS)
				.writeTimeout(20, TimeUnit.SECONDS)
				.addInterceptor(LoadCookiesInterceptor())
				.addInterceptor(SaveCookiesInterceptor())
				.addInterceptor(DebugInterceptor())
				.build()
	}

	private val qiniuClient by lazy {
		OkHttpClient.Builder()
				.retryOnConnectionFailure(true)
				.connectTimeout(15, TimeUnit.SECONDS)
				.build()
	}

	val retrofit by lazy {
		Retrofit.Builder()
				.baseUrl("https://xhuschedule.mostpan.com")
				.client(client)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build()!!
	}

	val splashLeanCloudRetrofit by lazy {
		Retrofit.Builder()
				.baseUrl("https://f939ktgh.api.lncld.net")
				.client(splashClient)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build()!!
	}

	val qiniuRetrofit by lazy {
		Retrofit.Builder()
				.baseUrl("https://download.xhuschedule.mostpan.com")
				.client(qiniuClient)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build()
	}

	val leanCloudRetrofit by lazy {
		Retrofit.Builder()
				.baseUrl("https://f939ktgh.api.lncld.net")
				.client(client)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build()!!
	}
}