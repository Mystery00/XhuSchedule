/*
 * Created by Mystery0 on 18-1-4 下午4:51.
 * Copyright (c) 2018. All Rights reserved.
 *
 * Last modified 18-1-4 下午4:51
 */

package com.weilylab.xhuschedule.interfaces

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserService {
    @FormUrlEncoded
    @POST("/User/autoLogin")
    fun autoLogin(@Field("username") username: String, @Field("password") password: String): Observable<ResponseBody>


}