package com.weilylab.xhuschedule.ui.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemShowCourseBinding
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
				.appendln(splitWeek(data.weekList))
				.append(courseTimeText)
		binding.textView.text = stringBuilder.toString()
		binding.textView.setLineSpacing(binding.textView.textSize * 0.6f, 1f)
		val gradientDrawable = GradientDrawable()
		gradientDrawable.setColor(data.extras["colorInt"] as Int)
		gradientDrawable.cornerRadius = 48F
		binding.linearLayout.background = gradientDrawable
	}

	private fun splitWeek(list: MutableList<Int>): String {
		val stringBuilder = StringBuilder()
		var start = list[0]
		var end = list[0]
		if (list.size == 1)
			return "$start-${end}周"
		for (i in 1 until list.size) {
			val next = list[i]
			if (next != end + 1) {
				stringBuilder.append("$start-${end}周 ")
				start = next
				end = next
			} else {
				end = next
			}
			if (i == list.size - 1)
				stringBuilder.append("$start-${end}周")
		}
		return stringBuilder.toString()
	}
}