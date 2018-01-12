/*
 * Created by Mystery0 on 18-1-4 下午4:50.
 * Copyright (c) 2018. All Rights reserved.
 *
 * Last modified 18-1-4 下午4:50
 */

package com.weilylab.xhuschedule.interfaces

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface CommonService {

    @GET("/xhuschedule/Common/feedback")
    fun feedback(@Query("username") username: String,
                 @Query("appVersion") appVersion: String,
                 @Query("systemVersion") systemVersion: String,
                 @Query("factory") vendor: String,
                 @Query("model") model: String,
                 @Query("rom") rom: String,
                 @Query("other") other: String,
                 @Query("message") message: String): Observable<ResponseBody>

    @GET("/xhuschedule/Common/getNotices")
    fun getNotices(@Query("platform") platform: String?): Observable<ResponseBody>
}