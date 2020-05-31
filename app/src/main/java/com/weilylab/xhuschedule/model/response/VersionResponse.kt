package com.weilylab.xhuschedule.model.response


import com.google.gson.annotations.SerializedName
import com.weilylab.xhuschedule.model.Version

data class VersionResponse(@SerializedName("code")
						   val code: Int = 0,
						   @SerializedName("data")
						   val data: Version,
						   @SerializedName("message")
						   val message: String = "")


