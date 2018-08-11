package com.weilylab.xhuschedule.newPackage.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

interface ScoreAPI {
	@FormUrlEncoded
	@POST("/Score/getScores")
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