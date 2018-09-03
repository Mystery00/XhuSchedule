package com.weilylab.xhuschedule.config

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.weilylab.xhuschedule.R
import com.zhuangfei.timetable.model.ScheduleColorPool

object ColorPoolHelper {
	@SuppressLint("StaticFieldLeak")
	val colorPool = ScheduleColorPool(APP.context)

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
//				R.color.colorPool23,
//				R.color.colorPool24,
//				R.color.colorPool25,
//				R.color.colorPool28,
//				R.color.colorPool29,
//				R.color.colorPool30,
//				R.color.colorPool31,
//				R.color.colorPool32,
//				R.color.colorPool33,
//				R.color.colorPool34,
//				R.color.colorPool35
		)
		colorArray.forEach {
			colorPool.add(ContextCompat.getColor(APP.context, it))
		}
		colorPool.uselessColor = Color.parseColor("#e5e5e5")
	}
}