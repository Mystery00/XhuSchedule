package com.weilylab.xhuschedule.newPackage.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserAPI {
	@FormUrlEncoded
	@POST("/User/autoLogin")
	fun autoLogin(@Field("username") username: String, @Field("password") password: String): Observable<ResponseBody>

	@FormUrlEncoded
	@POST("/User/getInfo")
	fun getInfo(@Field("username") username: String): Observable<ResponseBody>
}