package com.weilylab.xhuschedule.model.response

import com.google.gson.annotations.SerializedName
import com.weilylab.xhuschedule.model.ExpScore

class ExpScoreResponse : BaseResponse() {
	@SerializedName("expscores")
	lateinit var expScores: List<ExpScore>
}