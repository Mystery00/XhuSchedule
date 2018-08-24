package com.weilylab.xhuschedule.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TestAPI {
	@FormUrlEncoded
	@POST("/Test/getTests")
	fun getTests(@Field("username") username: String, @Field("year") year: String = "2016-2017", @Field("term") term: String = "2"): Observable<ResponseBody>
}