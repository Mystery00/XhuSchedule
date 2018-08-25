package com.weilylab.xhuschedule.ui.adapter

import android.content.Context
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.config.ColorPoolHelper
import com.weilylab.xhuschedule.databinding.ItemShowCourseBinding
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.tools.base.BaseBindingRecyclerViewAdapter

class ShowCourseRecyclerViewAdapter(context: Context?) : BaseBindingRecyclerViewAdapter<Schedule, ItemShowCourseBinding>(R.layout.item_show_course) {
	override fun setItemView(binding: ItemShowCourseBinding, position: Int, data: Schedule) {
		val stringBuilder = StringBuilder()
		stringBuilder.appendln(data.name)
		stringBuilder.appendln(data.teacher)
		stringBuilder.appendln(data.room)
		binding.textView.text = stringBuilder.toString()
		val color = ColorPoolHelper.colorPool.getColorAuto(data.colorRandom)
		binding.textView.setBackgroundColor(color)
	}
}