package com.weilylab.xhuschedule.api

import com.weilylab.xhuschedule.model.Splash
import com.weilylab.xhuschedule.model.response.CloudResponse
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface XhuScheduleCloudAPI {
	@FormUrlEncoded
	@POST("/9783/api/rest/v1/xhuschedulecloud/version")
	fun checkVersion(@Field("appVersion") appVersion: String,
					 @Field("systemVersion") systemVersion: String,
					 @Field("factory") vendor: String,
					 @Field("model") model: String,
					 @Field("rom") rom: String,
					 @Field("device_id") deviceID: String): Observable<ResponseBody>

	@GET("/9783/api/rest/v1/xhuschedulecloud/schoolcalendar/url")
	fun schoolCalendar(): Observable<ResponseBody>

	@GET("/9783/api/rest/v1/xhuschedulecloud/splash")
	fun requestSplashInfo(): Call<CloudResponse<Splash>>
}