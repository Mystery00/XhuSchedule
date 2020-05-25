package com.weilylab.xhuschedule.api

import com.weilylab.xhuschedule.model.response.GetFeedBackMessageResponse
import com.weilylab.xhuschedule.model.response.SendFeedBackMessageResponse
import io.reactivex.Observable
import okhttp3.ResponseBody
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