package com.weilylab.xhuschedule.model.response

import com.weilylab.xhuschedule.model.FeedBackMessage

class GetFeedBackMessageResponse : BaseResponse() {
	lateinit var fBMessage: List<FeedBackMessage>
}