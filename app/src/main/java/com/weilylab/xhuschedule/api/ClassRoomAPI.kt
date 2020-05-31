/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.api

import com.weilylab.xhuschedule.model.response.ClassroomResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ClassRoomAPI {
	@FormUrlEncoded
	@POST("/Classroom/getClassrooms")
	suspend fun getClassrooms(@Field("username") username: String,
							  @Field("location") location: String,
							  @Field("week") week: String,
							  @Field("day") day: String,
							  @Field("time") time: String): ClassroomResponse
}