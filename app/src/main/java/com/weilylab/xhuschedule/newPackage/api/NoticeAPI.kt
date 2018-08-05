package com.weilylab.xhuschedule.newPackage.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface NoticeAPI {
	@GET("/Common/getNotices")
	fun getNotices(@Query("platform") platform: String?): Observable<ResponseBody>
}