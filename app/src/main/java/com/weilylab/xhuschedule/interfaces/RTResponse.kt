package com.weilylab.xhuschedule.interfaces

import com.weilylab.xhuschedule.classes.ContentRT
import io.reactivex.Observable
import okhttp3.ResponseBody

import retrofit2.Call
import retrofit2.http.*

/**
 * Created by myste.
 */

interface RTResponse {

    @GET("/course/Course/getContent")
    fun getContentCall(): Call<ContentRT>

    @FormUrlEncoded
    @POST("/course/Course/getCourses")
    fun getCourses(@Field("username")username: String,@Field("password")password: String):Observable<ResponseBody>
}
