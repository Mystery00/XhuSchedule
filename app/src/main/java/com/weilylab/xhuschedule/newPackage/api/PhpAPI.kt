package com.weilylab.xhuschedule.newPackage.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface PhpAPI {
	@FormUrlEncoded
	@POST("/9783/interface/checkVersion.php")
	fun checkVersion(@Field("appVersion") appVersion: String,
					 @Field("systemVersion") systemVersion: String,
					 @Field("factory") vendor: String,
					 @Field("model") model: String,
					 @Field("rom") rom: String): Observable<ResponseBody>
}