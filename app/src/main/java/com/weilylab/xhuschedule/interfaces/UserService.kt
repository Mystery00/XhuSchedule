/*
 * Created by Mystery0 on 17-11-30 上午11:21.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-30 上午11:21
 */

package com.weilylab.xhuschedule.interfaces

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserService {
    @FormUrlEncoded
    @POST("/course/Course/autoLogin")
    fun autoLogin(@Field("username") username: String, @Field("password") password: String): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("/course/User/getInfo")
    fun getInfo(@Field("username") username: String): Observable<ResponseBody>
}