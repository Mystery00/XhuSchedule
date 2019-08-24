package com.weilylab.xhuschedule.model.response

open class CloudResponse<T> {
	val code: Int = -1
	var data: T? = null
	lateinit var message: String
}