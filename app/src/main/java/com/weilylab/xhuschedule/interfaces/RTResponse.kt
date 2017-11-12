package com.weilylab.xhuschedule.interfaces

import com.weilylab.xhuschedule.classes.ContentRT
import com.weilylab.xhuschedule.classes.LoginRT
import com.weilylab.xhuschedule.classes.RT
import okhttp3.ResponseBody

import retrofit2.Call
import retrofit2.http.*

/**
 * Created by myste.
 */

interface RTResponse
{
	@Streaming
	@GET("/course/Course/getVCode")
	fun getVCodeCall(@Query("type") type: Int): Call<ResponseBody>

	@FormUrlEncoded
	@POST("/course/Course/login")
	fun loginCall(@Field("username") username: String, @Field("password") password: String, @Field("vcode") vcode: String): Call<LoginRT>

	@FormUrlEncoded
	@GET("/course/Course/getContent")
	fun getContentCall(): Call<ContentRT>
}
