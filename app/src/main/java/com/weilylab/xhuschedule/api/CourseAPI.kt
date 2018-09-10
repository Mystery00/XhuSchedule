package com.weilylab.xhuschedule.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CourseAPI {
	@FormUrlEncoded
	@POST("/Course/getCourses")
	fun getCourses(@Field("username") username: String, @Field("year") year: String?, @Field("term") term: String?): Observable<ResponseBody>
}