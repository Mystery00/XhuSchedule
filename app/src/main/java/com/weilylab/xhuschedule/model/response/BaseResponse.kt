package com.weilylab.xhuschedule.model.response

import com.weilylab.xhuschedule.constant.ResponseCodeConstants

abstract class BaseResponse {
	lateinit var msg: String
	lateinit var rt: String

	val isSuccessful: Boolean get() = rt == ResponseCodeConstants.DONE
}