package com.weilylab.xhuschedule.model.jrsc

import com.google.gson.annotations.SerializedName

data class Origin(
		@SerializedName("author")
		var author: String, // 李商隐
		@SerializedName("content")
		var content: List<String>,
		@SerializedName("dynasty")
		var dynasty: String, // 唐代
		@SerializedName("title")
		var title: String, // 夜雨寄北
		@SerializedName("translate")
		var translate: List<String>
)