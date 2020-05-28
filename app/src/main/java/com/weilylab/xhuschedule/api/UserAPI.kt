package com.weilylab.xhuschedule.api

import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.model.response.GetUserDataResponse
import com.weilylab.xhuschedule.model.response.LoginResponse
import com.weilylab.xhuschedule.model.response.SetUserDataResponse
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
	suspend fun getInfo(@Field("username") username: String): StudentInfo

	@FormUrlEncoded
	@POST("/User/setUserData")
	suspend fun setUserData(@Field("username") username: String, @Field("key") key: String, @Field("value") value: String, @Field("platform") platform: String = "Android"): SetUserDataResponse

	@FormUrlEncoded
	@POST("/User/getUserData")
	fun getUserData(@Field("username") username: String, @Field("key") key: String, @Field("platform") platform: String = "Android"): GetUserDataResponse

	@FormUrlEncoded
	@POST("/User/delUserData")
	suspend fun delUserData(@Field("username") username: String, @Field("key") key: String, @Field("platform") platform: String = "Android"): Observable<ResponseBody>
}