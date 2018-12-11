package com.weilylab.xhuschedule.model.jrsc

import com.google.gson.annotations.SerializedName

data class Data(
		@SerializedName("cacheAt")
		var cacheAt: String, // 2018-09-17T21:18:44.693645
		@SerializedName("content")
		var content: String, // 君问归期未有期，巴山夜雨涨秋池。
		@SerializedName("id")
		var id: String, // 5b8b9572e116fb3714e6faba
		@SerializedName("matchTags")
		var matchTags: List<String>,
		@SerializedName("origin")
		var origin: Origin,
		@SerializedName("popularity")
		var popularity: Int, // 1170000
		@SerializedName("recommendedReason")
		var recommendedReason: String
)