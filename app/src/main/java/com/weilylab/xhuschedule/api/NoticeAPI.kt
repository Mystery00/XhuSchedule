/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.api

import com.weilylab.xhuschedule.model.response.NoticeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NoticeAPI {
	@GET("/Common/getNotices")
	suspend fun getNotices(@Query("platform") platform: String?): NoticeResponse
}