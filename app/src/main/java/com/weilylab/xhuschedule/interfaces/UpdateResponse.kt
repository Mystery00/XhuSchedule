package com.weilylab.xhuschedule.interfaces

import com.weilylab.xhuschedule.classes.Update
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import rx.Observable

/**
 * Created by myste.
 */
interface UpdateResponse
{
	@FormUrlEncoded
	@POST("/XhuSchedule/interface/checkUpdate.php")
	fun checkUpdateCall(@Field("currentVersion") currentVersion: Int): Call<Update>

	@Streaming
	@GET("/XhuSchedule/{type}/{fileName}")
	fun download(@Path("type") type: String, @Path("fileName") fileName: String): Observable<ResponseBody>
}