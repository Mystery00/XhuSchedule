package com.weilylab.xhuschedule.api

import com.weilylab.xhuschedule.model.response.NoticeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NoticeAPI {
	@GET("/Common/getNotices")
	suspend fun getNotices(@Query("platform") platform: String?): NoticeResponse
}