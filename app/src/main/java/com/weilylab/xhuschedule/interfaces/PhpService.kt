/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-20 下午8:17
 */

package com.weilylab.xhuschedule.interfaces

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Created by myste.
 */
interface PhpService {
    @Streaming
    @FormUrlEncoded
    @POST("/XhuSchedule/interface/checkUpdate.php")
    fun checkUpdateCall(@Field("currentVersion") currentVersion: Int): Observable<ResponseBody>

    @Streaming
    @GET("/XhuSchedule/{type}/{fileName}")
    fun download(@Path("type") type: String, @Path("fileName") fileName: String): Observable<ResponseBody>

    @Multipart
    @POST("/XhuSchedule/interface/upload_log.php")
    fun uploadLog(@PartMap partMap: MutableMap<String, RequestBody>, @Part logFile: MultipartBody.Part): Observable<ResponseBody>

    @GET("/{fileName}")
    fun downloadImg(@Path("fileName") fileName: String): Observable<ResponseBody>
}