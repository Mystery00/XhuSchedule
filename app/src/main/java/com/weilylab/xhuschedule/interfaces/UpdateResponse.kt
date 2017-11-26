/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-22 上午2:23
 */

package com.weilylab.xhuschedule.interfaces

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Created by myste.
 */
interface UpdateResponse {
    @Streaming
    @FormUrlEncoded
    @POST("/XhuSchedule/interface/checkUpdate.php")
    fun checkUpdateCall(@Field("currentVersion") currentVersion: Int): Observable<ResponseBody>

    @Streaming
    @GET("/XhuSchedule/{type}/{fileName}")
    fun download(@Path("type") type: String, @Path("fileName") fileName: String): Observable<ResponseBody>
}