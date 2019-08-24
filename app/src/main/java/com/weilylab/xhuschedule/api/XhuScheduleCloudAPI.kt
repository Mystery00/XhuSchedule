package com.weilylab.xhuschedule.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

interface XhuScheduleCloudAPI {
	@FormUrlEncoded
	@POST("/9783/api/rest/v1/xhuschedulecloud/version")
	fun checkVersion(@Field("appVersion") appVersion: String,
					 @Field("systemVersion") systemVersion: String,
					 @Field("factory") vendor: String,
					 @Field("model") model: String,
					 @Field("rom") rom: String,
					 @Field("deviceID") deviceID: String): Observable<ResponseBody>

	@GET("/9783/api/rest/v1/xhuschedulecloud/schoolcalendar/url")
	fun schoolCalendar(): Observable<ResponseBody>

	@GET("/9783/api/rest/v1/xhuschedulecloud/splash")
	fun requestSplashInfo(): Observable<ResponseBody>

	@GET("/9783/api/rest/v1/xhuschedulecloud/starttime")
	fun requestStartDateTime(): Observable<ResponseBody>
}