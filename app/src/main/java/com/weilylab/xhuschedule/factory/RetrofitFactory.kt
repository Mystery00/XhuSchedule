package com.weilylab.xhuschedule.factory

import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.interceptor.DebugInterceptor
import com.weilylab.xhuschedule.interceptor.LoadCookiesInterceptor
import com.weilylab.xhuschedule.interceptor.SaveCookiesInterceptor
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.response.BaseResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

suspend fun <T : BaseResponse> T.verifyData(needLogin: suspend () -> T): T = when (rt) {
	ResponseCodeConstants.DONE -> this
	ResponseCodeConstants.ERROR_NOT_LOGIN -> needLogin()
	else -> throw Exception(msg)
}

suspend fun <T : BaseResponse> T.redoAfterLogin(student: Student, repeat: suspend () -> T): T = verifyData {
	student.reLogin {
		val response = repeat()
		if (!response.isSuccessful)
			throw Exception(response.msg)
		response
	}
}

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
				.addConverterFactory(GsonConverterFactory.create())
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