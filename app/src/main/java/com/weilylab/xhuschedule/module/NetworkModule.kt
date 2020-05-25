package com.weilylab.xhuschedule.module

import com.weilylab.xhuschedule.api.*
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.interceptor.DebugInterceptor
import com.weilylab.xhuschedule.interceptor.LoadCookiesInterceptor
import com.weilylab.xhuschedule.interceptor.SaveCookiesInterceptor
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.response.BaseResponse
import com.weilylab.xhuschedule.repository.LoginRepository
import okhttp3.OkHttpClient
import org.koin.core.inject
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
	single(named("client")) {
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
	single(named("fileClient")) {
		OkHttpClient.Builder()
				.retryOnConnectionFailure(true)
				.connectTimeout(15, TimeUnit.SECONDS)
				.build()
	}

	single(named("retrofit")) {
		Retrofit.Builder()
				.baseUrl(Constants.SERVER_URL)
				.client(get(named("client")))
				.addConverterFactory(GsonConverterFactory.create())
				.build()
	}
	single(named("fileRetrofit")) {
		Retrofit.Builder()
				.baseUrl("https://download.xhuschedule.mostpan.com")
				.client(get(named("fileClient")))
				.build()
	}
	single {
		get<Retrofit>(named("retrofit")).create(XhuScheduleCloudAPI::class.java)
	}
	single {
		get<Retrofit>(named("retrofit")).create(UserAPI::class.java)
	}
	single {
		get<Retrofit>(named("retrofit")).create(CourseAPI::class.java)
	}
	single {
		get<Retrofit>(named("retrofit")).create(NoticeAPI::class.java)
	}
	single {
		get<Retrofit>(named("retrofit")).create(FeedbackAPI::class.java)
	}
}

suspend fun <T : BaseResponse> T.verifyData(needLogin: suspend () -> T): T = when (rt) {
	ResponseCodeConstants.DONE -> this
	ResponseCodeConstants.ERROR_NOT_LOGIN -> needLogin()
	else -> throw Exception(msg)
}

suspend fun <T : BaseResponse> T.redoAfterLogin(student: Student, repeat: suspend () -> T): T = verifyData {
	val loginRepository: LoginRepository by inject()
	loginRepository.doLoginOnly(student)
	val response = repeat()
	if (!response.isSuccessful)
		throw Exception(response.msg)
	response
}