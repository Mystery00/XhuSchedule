package com.weilylab.xhuschedule.interfaces

import com.weilylab.xhuschedule.classes.Update
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by myste.
 */
interface UpdateResponse
{
	@FormUrlEncoded
	@POST("/XhuSchedule/interface/checkUpdate.php")
	fun checkUpdateCall(@Field("currentVersion") currentVersion: Int): Call<Update>
}