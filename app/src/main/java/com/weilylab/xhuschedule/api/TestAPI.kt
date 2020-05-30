package com.weilylab.xhuschedule.api

import com.weilylab.xhuschedule.model.response.TestResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TestAPI {
	@FormUrlEncoded
	@POST("/Test/getTests")
	suspend fun getTests(@Field("username") username: String): TestResponse
}