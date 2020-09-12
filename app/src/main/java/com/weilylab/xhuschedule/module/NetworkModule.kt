/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.module

import com.weilylab.xhuschedule.api.*
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.interceptor.DebugInterceptor
import com.weilylab.xhuschedule.interceptor.JinrishiciInterceptor
import com.weilylab.xhuschedule.interceptor.LoadCookiesInterceptor
import com.weilylab.xhuschedule.interceptor.SaveCookiesInterceptor
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.response.BaseResponse
import com.weilylab.xhuschedule.model.response.CloudResponse
import com.weilylab.xhuschedule.repository.StudentRepository
import okhttp3.OkHttpClient
import org.koin.core.inject
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vip.mystery0.tools.ResourceException
import vip.mystery0.tools.utils.isConnectInternet
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
	single(named("jrscClient")) {
		OkHttpClient.Builder()
				.addInterceptor(JinrishiciInterceptor())
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
	single(named("jrscRetrofit")) {
		Retrofit.Builder()
				.baseUrl(Constants.SERVER_JRSC)
				.client(get(named("jrscClient")))
				.addConverterFactory(GsonConverterFactory.create())
				.build()
	}
	single(named("fileRetrofit")) {
		Retrofit.Builder()
				.baseUrl(Constants.SERVER_QINIU)
				.client(get(named("fileClient")))
				.build()
	}
	single {
		get<Retrofit>(named("retrofit")).create(CourseAPI::class.java)
	}
	single {
		get<Retrofit>(named("retrofit")).create(FeedbackAPI::class.java)
	}
	single {
		get<Retrofit>(named("retrofit")).create(NoticeAPI::class.java)
	}
	single {
		get<Retrofit>(named("fileRetrofit")).create(QiniuAPI::class.java)
	}
	single {
		get<Retrofit>(named("retrofit")).create(ScoreAPI::class.java)
	}
	single {
		get<Retrofit>(named("retrofit")).create(TestAPI::class.java)
	}
	single {
		get<Retrofit>(named("retrofit")).create(UserAPI::class.java)
	}
	single {
		get<Retrofit>(named("retrofit")).create(XhuScheduleCloudAPI::class.java)
	}
	single {
		get<Retrofit>(named("jrscRetrofit")).create(JinrishiciAPI::class.java)
	}
}

suspend fun <T : BaseResponse> T.verifyData(needLogin: suspend () -> T): T = when (rt) {
	ResponseCodeConstants.DONE -> this
	ResponseCodeConstants.ERROR_NOT_LOGIN -> needLogin()
	else -> throw Exception(msg)
}

suspend fun <T : BaseResponse> T.redoAfterLogin(student: Student, repeat: suspend () -> T): T = verifyData {
	val studentRepository: StudentRepository by inject()
	studentRepository.doLoginOnly(student)
	val response = repeat()
	if (!response.isSuccessful)
		throw Exception(response.msg)
	response
}

suspend fun <T : CloudResponse> T.redoAfterLogin(student: Student, repeat: suspend () -> T): T {
	if (isSuccessful) {
		return this
	}
	if (code == ResponseCodeConstants.ERROR_NOT_LOGIN_CODE) {
		val studentRepository: StudentRepository by inject()
		studentRepository.doLoginOnly(student)
		val response = repeat()
		if (!response.isSuccessful)
			throw Exception(response.message)
		return response
	} else {
		throw Exception(message)
	}
}

fun <T : BaseResponse> T.check(): T = if (isSuccessful) this else throw Exception(msg)

fun <T : CloudResponse> T.check(): T = if (isSuccessful) this else throw Exception(message)

suspend fun <R> checkConnect(block: suspend () -> R): R {
	if (isConnectInternet()) {
		return block()
	} else {
		throw ResourceException(com.weilylab.xhuschedule.R.string.hint_network_error)
	}
}