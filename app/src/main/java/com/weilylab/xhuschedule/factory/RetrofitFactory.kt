package com.weilylab.xhuschedule.factory

import com.weilylab.xhuschedule.interceptor.DebugInterceptor
import com.weilylab.xhuschedule.interceptor.LoadCookiesInterceptor
import com.weilylab.xhuschedule.interceptor.SaveCookiesInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

object RetrofitFactory {
	private val client by lazy {
		OkHttpClient.Builder()
				.retryOnConnectionFailure(true)
				.connectTimeout(25, TimeUnit.SECONDS)
				.readTimeout(25, TimeUnit.SECONDS)
				.writeTimeout(25, TimeUnit.SECONDS)
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

	val retrofit: Retrofit by lazy {
		Retrofit.Builder()
				.baseUrl("https://xhuschedule.mystery0.app")
				.client(client)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build()
	}

	val feedbackRetrofit: Retrofit by lazy {
		Retrofit.Builder()
				.baseUrl("https://xhuschedule.mystery0.app")
				.client(client)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build()
	}

	val qiniuRetrofit: Retrofit by lazy {
		Retrofit.Builder()
				.baseUrl("https://download.xhuschedule.mostpan.com")
				.client(qiniuClient)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build()
	}
}