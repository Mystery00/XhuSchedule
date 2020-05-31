/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemShowCourseBinding
import com.weilylab.xhuschedule.utils.userDo.CourseUtil
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.tools.base.binding.BaseBindingRecyclerViewAdapter

class ShowCourseRecyclerViewAdapter(private val context: Context) : BaseBindingRecyclerViewAdapter<Schedule, ItemShowCourseBinding>(R.layout.item_show_course) {
	override fun setItemView(binding: ItemShowCourseBinding, position: Int, data: Schedule) {
		binding.textViewTitle.text = data.name
		val startTimeArray = context.resources.getStringArray(R.array.start_time)
		val endTimeArray = context.resources.getStringArray(R.array.end_time)
		val courseTimeText = "${startTimeArray[data.start - 1]} - ${endTimeArray[data.start + data.step - 2]}"
		val stringBuilder = StringBuilder()
		stringBuilder.appendln(data.teacher)
				.appendln(data.room)
				.append(CourseUtil.splitWeekString(data.weekList).replace(",", "周 ")).appendln("周")
				.append(courseTimeText)
		binding.textView.text = stringBuilder.toString()
		binding.textView.setLineSpacing(binding.textView.textSize * 0.6f, 1f)
		val gradientDrawable = GradientDrawable()
		gradientDrawable.setColor(data.extras["colorInt"] as Int)
		gradientDrawable.cornerRadius = 48F
		binding.linearLayout.background = gradientDrawable
	}
}