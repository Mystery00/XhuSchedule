/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.api

import com.weilylab.xhuschedule.model.LoginParam
import com.weilylab.xhuschedule.model.response.*
import retrofit2.http.*

interface XhuScheduleCloudAPI {
	@FormUrlEncoded
	@POST("/api/rest/v1/xhuschedulecloud/version")
	suspend fun checkVersion(@Field("appVersion") appVersion: String,
							 @Field("systemVersion") systemVersion: String,
							 @Field("factory") vendor: String,
							 @Field("model") model: String,
							 @Field("rom") rom: String,
							 @Field("deviceID") deviceID: String): VersionResponse

	@GET("/api/rest/v1/xhuschedulecloud/schoolcalendar/url")
	suspend fun schoolCalendar(): SchoolCalendarResponse

	@GET("/api/rest/v1/xhuschedulecloud/splash")
	suspend fun requestSplashInfo(): SplashResponse

	@GET("/api/rest/v1/xhuschedulecloud/starttime")
	suspend fun requestStartDateTime(): StartDateTimeResponse

	@FormUrlEncoded
	@POST("/api/rest/v1/xhuschedulecloud/publicKey")
	suspend fun getPublicKey(@Field("username") username: String): PublicKeyResponse

	@POST("/api/rest/v1/xhuschedulecloud/login")
	suspend fun login(@Body loginParam: LoginParam): LoginResponse
}