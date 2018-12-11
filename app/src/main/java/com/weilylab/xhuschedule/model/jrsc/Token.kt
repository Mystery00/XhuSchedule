package com.weilylab.xhuschedule.model.jrsc

import com.google.gson.annotations.SerializedName

data class Token(
		@SerializedName("data")
		var token: String, // RgU1rBKtLym/MhhYIXs42WNoqLyZeXY3EkAcDNrcfKkzj8ILIsAP1Hx0NGhdOO1I
		@SerializedName("status")
		var status: String // success
)