package com.weilylab.xhuschedule.newPackage.utils

import android.app.Activity
import android.content.Context

object AnimationUtil {
	fun setWindowAlpha(context: Context?, alpha: Float) {
		val layoutParams = (context as Activity).window.attributes
		layoutParams.alpha = alpha
		context.window.attributes = layoutParams
	}
}