package com.weilylab.xhuschedule.ui.custom

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.widget.FrameLayout
import android.widget.TextView
import com.weilylab.xhuschedule.config.APP
import com.zhuangfei.timetable.listener.OnItemBuildAdapter
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.tools.utils.DensityTools

class CustomItemBuildAdapter : OnItemBuildAdapter() {
	var week = 0
	private val counterSize = DensityTools.dp2px(APP.context, 10F)

	override fun onItemUpdate(layout: FrameLayout?, textView: TextView, countTextView: TextView, schedule: Schedule, gd: GradientDrawable?) {
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
		textView.setLineSpacing(textView.textSize * 0.4f, 1f)
		val layoutParams = countTextView.layoutParams
		layoutParams.width = counterSize
		layoutParams.height = counterSize
		countTextView.layoutParams = layoutParams
		countTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8f)
		if (!schedule.weekList.contains(week))
			textView.setTextColor(Color.parseColor("#888888"))
		else
			textView.setTextColor(Color.WHITE)
	}
}