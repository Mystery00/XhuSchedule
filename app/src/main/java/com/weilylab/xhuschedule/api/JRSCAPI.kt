package com.weilylab.xhuschedule.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET

interface JRSCAPI {
	@GET("/token")
	fun getToken(): Observable<ResponseBody>

	@GET("/one.json")
	fun getJson(): Observable<ResponseBody>
}