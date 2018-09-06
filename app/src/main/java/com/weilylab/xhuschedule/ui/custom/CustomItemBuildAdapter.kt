package com.weilylab.xhuschedule.ui.custom

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.config.APP
import com.weilylab.xhuschedule.config.ColorPoolHelper
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.zhuangfei.timetable.TimetableView
import com.zhuangfei.timetable.listener.OnItemBuildAdapter
import com.zhuangfei.timetable.model.Schedule
import com.zhuangfei.timetable.model.ScheduleSupport
import com.zhuangfei.timetable.utils.ColorUtils
import vip.mystery0.tools.utils.DensityTools

class CustomItemBuildAdapter(private val context: Context,
							 private val timetableView: TimetableView) : OnItemBuildAdapter() {
	var week = 0
	private val counterSize = DensityTools.dp2px(APP.context, 8F)

	override fun onItemUpdate(layout: FrameLayout, textView: TextView, countTextView: TextView, schedule: Schedule, gd: GradientDrawable) {
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
		textView.setLineSpacing(DensityTools.dp2px(context,3), 1f)
		val layoutParams = countTextView.layoutParams
		layoutParams.width = counterSize
		layoutParams.height = counterSize
		countTextView.layoutParams = layoutParams
		countTextView.text = ""
		if (!schedule.weekList.contains(week)) {
			textView.setTextColor(Color.parseColor("#888888"))
			gd.setColor(ColorPoolHelper.colorPool.getUselessColorWithAlpha(timetableView.itemAlpha()))
		} else {
			textView.setTextColor(Color.WHITE)
			gd.setColor(ColorUtils.alphaColor(schedule.extras["colorInt"] as Int, timetableView.itemAlpha()))
		}
		if (ConfigurationUtil.isShowNotWeek) {
			val todayCourses = ScheduleSupport.getAllSubjectsWithDay(timetableView.dataSource(), schedule.day - 1)
			val originCourseList = ScheduleSupport.findSubjects(schedule, todayCourses)
			if (originCourseList.size > 1) {
				val imageView = ImageView(context)
				imageView.setImageResource(R.mipmap.ic_radius_cell)
				val params = FrameLayout.LayoutParams(20, 20)
				params.gravity = Gravity.END or Gravity.BOTTOM
				layout.addView(imageView, params)
			}
		}
	}
}