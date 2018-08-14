package com.weilylab.xhuschedule.listener

interface DoSaveListener<T> {
	fun doSave(t: T)
}