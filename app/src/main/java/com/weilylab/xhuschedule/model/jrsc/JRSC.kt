package com.weilylab.xhuschedule.model.jrsc

import com.google.gson.annotations.SerializedName

data class JRSC(
		@SerializedName("data")
		var content: Data,
		@SerializedName("status")
		var status: String, // success
		@SerializedName("token")
		var token: String // 6453911a-9ad7-457e-9b9d-c21011b85a0c
)