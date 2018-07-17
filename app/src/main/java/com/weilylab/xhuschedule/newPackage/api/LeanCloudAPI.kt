package com.weilylab.xhuschedule.newPackage.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Query

interface LeanCloudAPI {
	@FormUrlEncoded
	@GET("/1.1/classes/Splash")
	fun requestSplashInfo(@Query("order") order: String = "-indexID", @Query("limit") limit: String = "1"): Observable<ResponseBody>
}