package com.weilylab.xhuschedule.newPackage.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming

interface PhpAPI {
	@Streaming
	@GET("/9783/interface/checkVersion.php")
	fun checkVersion(): Observable<ResponseBody>
}