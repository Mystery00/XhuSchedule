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
    @POST("/xhuschedule/Course/getCourses")
    fun getCourses(@Field("username") username: String, @Field("year") year: String?, @Field("term") term: Int?): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("/xhuschedule/Test/getTests")
    fun getTests(@Field("username") username: String): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("/xhuschedule/Score/getScores")
    fun getScores(@Field("username") username: String, @Field("year") year: String?, @Field("term") term: Int?): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("/xhuschedule/Score/getExpScores")
    fun getExpScores(@Field("username") username: String, @Field("year") year: String?, @Field("term") term: Int?): Observable<ResponseBody>
}