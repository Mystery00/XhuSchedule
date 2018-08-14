package com.weilylab.xhuschedule.config

import android.animation.ObjectAnimator

object SpaceScheduleHelper {
	var animation: ObjectAnimator? = null
	var onSpaceScheduleClickListener: ((Int, Int, Boolean) -> Unit)? = null
}