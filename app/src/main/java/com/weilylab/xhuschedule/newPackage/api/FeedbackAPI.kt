package com.weilylab.xhuschedule.newPackage.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface FeedbackAPI {
	@GET("/Common/feedback")
	fun feedback(@Query("username") username: String,
				 @Query("appVersion") appVersion: String,
				 @Query("systemVersion") systemVersion: String,
				 @Query("factory") vendor: String,
				 @Query("model") model: String,
				 @Query("rom") rom: String,
				 @Query("other") other: String,
				 @Query("message") message: String): Observable<ResponseBody>
}