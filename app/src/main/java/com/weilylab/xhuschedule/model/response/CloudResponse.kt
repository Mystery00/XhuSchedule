package com.weilylab.xhuschedule.model.response

abstract class CloudResponse {
	val code: Int = -1
	lateinit var message: String

	val isSuccessful: Boolean
		get() = code == 0
}