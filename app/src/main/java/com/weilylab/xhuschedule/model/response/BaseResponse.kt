package com.weilylab.xhuschedule.model.response

import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import org.koin.core.KoinComponent

abstract class BaseResponse : KoinComponent {
	lateinit var msg: String
	lateinit var rt: String

	val isSuccessful: Boolean get() = rt == ResponseCodeConstants.DONE
}