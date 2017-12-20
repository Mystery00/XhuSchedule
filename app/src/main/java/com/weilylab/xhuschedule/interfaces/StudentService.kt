/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-21 上午2:41
 */

package com.weilylab.xhuschedule.interfaces

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

interface StudentService {
    @FormUrlEncoded
    @POST("/course/Course/autoLogin")
    fun autoLogin(@Field("username") username: String, @Field("password") password: String): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("/course/Course/getCourses")
    fun getCourses(@Field("username") username: String, @Field("year") year: String?, @Field("term") term: Int?): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("/course/User/getInfo")
    fun getInfo(@Field("username") username: String): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("/course/Course/getTests")
    fun getTests(@Field("username") username: String): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("/course/Course/getScores")
    fun getScores(@Field("username") username: String, @Field("year") year: String?, @Field("term") term: Int?): Observable<ResponseBody>

    @GET("/course/Common/feedback")
    fun feedback(@Query("username") username: String,
                 @Query("appVersion") appVersion: String,
                 @Query("systemVersion") systemVersion: String,
                 @Query("factory") vendor: String,
                 @Query("model") model: String,
                 @Query("rom") rom: String,
                 @Query("other") other: String,
                 @Query("message") message: String): Observable<ResponseBody>
}