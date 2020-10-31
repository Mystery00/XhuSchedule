/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.api

import com.weilylab.xhuschedule.model.response.BaseResponse
import com.weilylab.xhuschedule.model.response.GetUserDataResponse
import com.weilylab.xhuschedule.model.response.SetUserDataResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserAPI {
    @FormUrlEncoded
    @POST("/User/setUserData")
    suspend fun setUserData(@Field("username") username: String, @Field("key") key: String, @Field("value") value: String, @Field("platform") platform: String = "Android"): SetUserDataResponse

    @FormUrlEncoded
    @POST("/User/getUserData")
    suspend fun getUserData(@Field("username") username: String, @Field("key") key: String, @Field("platform") platform: String = "Android"): GetUserDataResponse

    @FormUrlEncoded
    @POST("/User/delUserData")
    suspend fun delUserData(@Field("username") username: String, @Field("key") key: String, @Field("platform") platform: String = "Android"): BaseResponse
}