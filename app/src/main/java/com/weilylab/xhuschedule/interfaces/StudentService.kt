/*
 * Created by Mystery0 on 18-2-21 下午9:12.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 18-2-21 下午9:11
 */

package com.weilylab.xhuschedule.interfaces

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

interface StudentService {
    @FormUrlEncoded
    @POST("/Course/getCourses")
    fun getCourses(@Field("username") username: String, @Field("year") year: String?, @Field("term") term: Int?): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("/Test/getTests")
    fun getTests(@Field("username") username: String): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("/Score/getScore")
    fun getScores(@Field("username") username: String, @Field("year") year: String?, @Field("term") term: Int?): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("/Score/getExpScores")
    fun getExpScores(@Field("username") username: String, @Field("year") year: String?, @Field("term") term: Int?): Observable<ResponseBody>

    @GET("/Score/getCETVCode")
    fun getCETVCode(@Query("username") username: String, @Query("id") id: String, @Query("type") type: String?): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("/Score/getCETScores")
    fun getCETScores(@Field("username") username: String, @Field("id") id: String, @Field("name") name: String, @Field("vcode") vcode: String): Observable<ResponseBody>
}