package com.weilylab.xhuschedule.config

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.weilylab.xhuschedule.R
import com.zhuangfei.timetable.model.ScheduleColorPool

object ColorPoolHelper {
	val colorPool by lazy { ScheduleColorPool(APP.context) }

	init {
		initColorPool(colorPool)
	}

	fun initColorPool(colorPool: ScheduleColorPool) {
		colorPool.clear()
		val colorArray = intArrayOf(
				R.color.colorPool1,
				R.color.colorPool15,
				R.color.colorPool16,
				R.color.colorPool17,
				R.color.colorPool18,
				R.color.colorPool19,
				R.color.colorPool20,
				R.color.colorPool21,
				R.color.colorPool22
		)
		colorArray.forEach {
			colorPool.add(ContextCompat.getColor(APP.context, it))
		}
		colorPool.uselessColor = Color.parseColor("#e5e5e5")
	}
}