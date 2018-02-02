/*
 * Created by Mystery0 on 18-1-12 下午8:52.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                  =====================================================
 *                  =                                                   =
 *                  =                       _oo0oo_                     =
 *                  =                      o8888888o                    =
 *                  =                      88" . "88                    =
 *                  =                      (| -_- |)                    =
 *                  =                      0\  =  /0                    =
 *                  =                    ___/`---'\___                  =
 *                  =                  .' \\|     |# '.                 =
 *                  =                 / \\|||  :  |||# \                =
 *                  =                / _||||| -:- |||||- \              =
 *                  =               |   | \\\  -  #/ |   |              =
 *                  =               | \_|  ''\---/''  |_/ |             =
 *                  =               \  .-\__  '-'  ___/-. /             =
 *                  =             ___'. .'  /--.--\  `. .'___           =
 *                  =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                  =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                  =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                  =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                  =                       `=---='                     =
 *                  =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                  =                                                   =
 *                  =               佛祖保佑         永无BUG              =
 *                  =                                                   =
 *                  =====================================================
 *
 * Last modified 18-1-12 下午8:51
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
    @POST("/interface/checkUpdate.php")
    fun checkUpdateCall(@Field("currentVersion") currentVersion: Int): Observable<ResponseBody>

    @Streaming
    @GET("/interface/checkVersion.php")
    fun checkVersion(): Observable<ResponseBody>

    @Streaming
    @GET("/{type}/{fileName}")
    fun download(@Path("type") type: String, @Path("fileName") fileName: String): Observable<ResponseBody>

    @Multipart
    @POST("/interface/upload_log.php")
    fun uploadLog(@PartMap partMap: MutableMap<String, RequestBody>, @Part logFile: MultipartBody.Part): Observable<ResponseBody>

    @GET("/{fileName}")
    fun downloadImg(@Path("fileName") fileName: String): Observable<ResponseBody>
}