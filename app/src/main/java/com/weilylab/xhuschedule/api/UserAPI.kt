package com.weilylab.xhuschedule.api

import com.weilylab.xhuschedule.model.response.LoginResponse
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserAPI {
	@FormUrlEncoded
	@POST("/User/autoLogin")
	suspend fun autoLogin(@Field("username") username: String, @Field("password") password: String): LoginResponse

	@FormUrlEncoded
	@POST("/User/getInfo")
	fun getInfo(@Field("username") username: String): Observable<ResponseBody>

	@FormUrlEncoded
	@POST("/User/setUserData")
	fun setUserData(@Field("username") username: String, @Field("key") key: String, @Field("value") value: String, @Field("platform") platform: String = "Android"): Observable<ResponseBody>

	@FormUrlEncoded
	@POST("/User/getUserData")
	fun getUserData(@Field("username") username: String, @Field("key") key: String, @Field("platform") platform: String = "Android"): Observable<ResponseBody>

	@FormUrlEncoded
	@POST("/User/delUserData")
	fun delUserData(@Field("username") username: String, @Field("key") key: String, @Field("platform") platform: String = "Android"): Observable<ResponseBody>
}