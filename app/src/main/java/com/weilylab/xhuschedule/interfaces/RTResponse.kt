/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-23 下午9:12
 */

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

    @FormUrlEncoded
    @POST("/course/Course/autoLogin")
    fun autoLogin(@Field("username") username: String, @Field("password") password: String): Observable<ResponseBody>
}
