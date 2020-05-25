package com.weilylab.xhuschedule.model.event

data class UIConfigEvent(val refreshUI: ArrayList<UI>)

enum class UI {
	TODAY_COURSE,
	TABLE_COURSE
}