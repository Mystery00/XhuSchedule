package com.weilylab.xhuschedule.listener

interface RequestListener<T> {
	fun done(t: T)

	fun error(rt: String, msg: String?)
}