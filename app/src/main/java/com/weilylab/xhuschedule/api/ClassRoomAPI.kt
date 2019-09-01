package com.weilylab.xhuschedule.api

import com.weilylab.xhuschedule.model.response.ClassroomResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ClassRoomAPI {
	@FormUrlEncoded
	@POST("/Classroom/getClassrooms")
	fun getClassrooms(@Field("username") username: String,
					  @Field("location") location: String,
					  @Field("week") week: String,
					  @Field("day") day: String,
					  @Field("time") time: String): Call<ClassroomResponse>
}