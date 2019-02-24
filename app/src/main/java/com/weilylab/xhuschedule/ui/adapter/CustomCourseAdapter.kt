package com.weilylab.xhuschedule.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemListCustomCourseBinding
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.utils.userDo.CourseUtil
import vip.mystery0.tools.base.binding.BaseBindingRecyclerViewAdapter

class CustomCourseAdapter(private val context: Context) : BaseBindingRecyclerViewAdapter<Course, ItemListCustomCourseBinding>(R.layout.item_list_custom_course) {
	private var clickListener: ((Course) -> Unit)? = null

	override fun setItemView(binding: ItemListCustomCourseBinding, position: Int, data: Course) {
		binding.course = data
		binding.root.backgroundTintList = ColorStateList.valueOf(Color.parseColor(data.color))
		val weekText = "第 ${CourseUtil.splitWeekString(data.week.split(",").map { it.toInt() })} 周"
		binding.textViewWeek.text = weekText
		val timeText = "第 ${data.time} 节"
		binding.textViewTime.text = timeText
		binding.root.setOnClickListener { clickListener?.invoke(data) }
	}

	fun setOnClickListener(listener: (Course) -> Unit) {
		this.clickListener = listener
	}
}