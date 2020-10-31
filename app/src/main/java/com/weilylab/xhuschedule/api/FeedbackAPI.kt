/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.api

import com.weilylab.xhuschedule.model.response.GetFeedBackMessageResponse
import com.weilylab.xhuschedule.model.response.SendFeedBackMessageResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface FeedbackAPI {
    @FormUrlEncoded
    @POST("/Common/sendFBMessage")
    suspend fun sendFBMessage(@Field("username") username: String,
                              @Field("fbToken") fbToken: String,
                              @Field("content") content: String,
                              @Field("platform") platform: String = "Android"): SendFeedBackMessageResponse

    @FormUrlEncoded
    @POST("/Common/getFBMessage")
    suspend fun getFBMessage(@Field("username") username: String,
                             @Field("fbToken") fbToken: String,
                             @Field("lastId") lastId: Int,
                             @Field("platform") platform: String = "Android"): GetFeedBackMessageResponse
}