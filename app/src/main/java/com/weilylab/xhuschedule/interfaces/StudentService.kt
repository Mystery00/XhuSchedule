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

interface StudentService {
    @FormUrlEncoded
    @POST("/course/Course/autoLogin")
    fun autoLogin(@Field("username") username: String, @Field("password") password: String): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("/course/Course/getCourses")
    fun getCourses(@Field("username") username: String): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("/course/User/getInfo")
    fun getInfo(@Field("username") username: String): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("/course/Course/getTests")
    fun getTests(@Field("username") username: String): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("/course/Course/getScores")
    fun getScores(@Field("username") username: String, @Field("year") year: String?, @Field("term") term: Int?): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("/course/Common/feedback")
    fun feedback(@Field("username") username: String,
                 @Field("appVersion") appVersion: String,
                 @Field("systemVersion") systemVersion: String,
                 @Field("factory") factory: String,
                 @Field("model") model: String,
                 @Field("rom") rom: String,
                 @Field("other") other: String,
                 @Field("message") message: String): Observable<ResponseBody>
}