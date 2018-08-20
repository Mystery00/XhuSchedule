package com.weilylab.xhuschedule.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Query

interface LeanCloudAPI {
	@GET("/1.1/classes/Splash")
	fun requestSplashInfo(@Query("order") order: String = "-indexID", @Query("limit") limit: String = "1"): Observable<ResponseBody>

	@GET("/1.1/classes/StartDateTime")
	fun requestStartDateTime(@Query("limit") limit: String = "1"): Observable<ResponseBody>
}