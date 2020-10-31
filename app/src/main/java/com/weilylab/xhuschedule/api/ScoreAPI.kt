/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.api

import com.weilylab.xhuschedule.model.response.CetScoresResponse
import com.weilylab.xhuschedule.model.response.CetVCodeResponse
import com.weilylab.xhuschedule.model.response.ClassScoreResponse
import com.weilylab.xhuschedule.model.response.ExpScoreResponse
import retrofit2.http.*

interface ScoreAPI {
    @FormUrlEncoded
    @POST("/Score/getScores")
    suspend fun getScores(@Field("username") username: String, @Field("year") year: String?, @Field("term") term: String?): ClassScoreResponse

    @FormUrlEncoded
    @POST("/Score/getExpScores")
    suspend fun getExpScores(@Field("username") username: String, @Field("year") year: String?, @Field("term") term: String?): ExpScoreResponse

    @GET("/Score/getCETVCode")
    suspend fun getCETVCode(@Query("username") username: String, @Query("id") id: String, @Query("type") type: String?): CetVCodeResponse

    @FormUrlEncoded
    @POST("/Score/getCETScores")
    suspend fun getCETScores(@Field("username") username: String, @Field("id") id: String, @Field("name") name: String, @Field("vcode") vcode: String): CetScoresResponse
}