package com.weilylab.xhuschedule.interfaces

import io.reactivex.Observable
import okhttp3.ResponseBody

import retrofit2.http.*

/**
 * Created by myste.
 */

interface RTResponse {

    @FormUrlEncoded
    @POST("/course/Course/getCourses")
    fun getCourses(@Field("username") username: String): Observable<ResponseBody>

    @GET("/course/Course/autoLogin")
    fun autoLogin(@Query("username") username: String, @Query("password") password: String): Observable<ResponseBody>
}
