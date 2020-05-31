package com.weilylab.xhuschedule.model.response

import com.google.gson.annotations.SerializedName
import com.weilylab.xhuschedule.model.ClassScore

class ClassScoreResponse : BaseResponse() {
	lateinit var scores: List<ClassScore>

	@SerializedName("failscores")
	lateinit var failScores: List<ClassScore>
}