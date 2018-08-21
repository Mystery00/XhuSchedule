package com.weilylab.xhuschedule.model.response

import com.google.gson.annotations.SerializedName
import com.weilylab.xhuschedule.model.CetScore

class CetScoresResponse:BaseResponse() {
	@SerializedName("scores")
	lateinit var cetScore: CetScore
}