package com.weilylab.xhuschedule.interfaces

import com.weilylab.xhuschedule.classes.RT
import okhttp3.ResponseBody

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Streaming

/**
 * Created by myste.
 */

interface RTResponse
{
	@Streaming
	@POST("/course/Course/getVCode")
	fun getVCodeCall(@Query("type") type: Int): Call<ResponseBody>

	@POST("/course/Course/login")
	fun loginCall(@Query("username") username: String, @Query("password") password: String, @Query("vcode") vcode: String): Call<ResponseBody>

	@POST("/course/Course/getContent")
	fun getContentCall(): Call<ResponseBody>
}
