package com.weilylab.xhuschedule.interfaces

import io.reactivex.Observable
import okhttp3.ResponseBody

import retrofit2.http.*

/**
 * Created by myste.
 */

interface RTResponse {

    @FormUrlEncoded
    @POST("/course/Course/getCourses")
    fun getCourses(@Field("username")username: String,@Field("password")password: String):Observable<ResponseBody>
}
