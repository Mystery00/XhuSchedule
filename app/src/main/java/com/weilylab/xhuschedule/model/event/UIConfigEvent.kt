package com.weilylab.xhuschedule.model.event

data class UIConfigEvent(val refreshUI: ArrayList<UI>)

enum class UI {
	MAIN_INIT,
	USER_IMG,
	BACKGROUND_IMG
}