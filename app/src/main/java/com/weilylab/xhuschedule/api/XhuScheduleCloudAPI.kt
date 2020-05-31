package com.weilylab.xhuschedule.api

import com.weilylab.xhuschedule.model.response.SchoolCalendarResponse
import com.weilylab.xhuschedule.model.response.SplashResponse
import com.weilylab.xhuschedule.model.response.StartDateTimeResponse
import com.weilylab.xhuschedule.model.response.VersionResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface XhuScheduleCloudAPI {
	@FormUrlEncoded
	@POST("/9783/api/rest/v1/xhuschedulecloud/version")
	suspend fun checkVersion(@Field("appVersion") appVersion: String,
							 @Field("systemVersion") systemVersion: String,
							 @Field("factory") vendor: String,
							 @Field("model") model: String,
							 @Field("rom") rom: String,
							 @Field("deviceID") deviceID: String): VersionResponse

	@GET("/9783/api/rest/v1/xhuschedulecloud/schoolcalendar/url")
	suspend fun schoolCalendar(): SchoolCalendarResponse

	@GET("/9783/api/rest/v1/xhuschedulecloud/splash")
	suspend fun requestSplashInfo(): SplashResponse

	@GET("/9783/api/rest/v1/xhuschedulecloud/starttime")
	suspend fun requestStartDateTime(): StartDateTimeResponse
}