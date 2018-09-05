package com.weilylab.xhuschedule.ui.adapter

import android.content.Context
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemFragmentTodayBinding
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.tools.base.binding.BaseBindingRecyclerViewAdapter

class FragmentTodayRecyclerViewAdapter(private val context: Context) : BaseBindingRecyclerViewAdapter<Schedule, ItemFragmentTodayBinding>(R.layout.item_fragment_today) {
	override fun setItemView(binding: ItemFragmentTodayBinding, position: Int, data: Schedule) {
		binding.course = data
		val color = data.extras["colorInt"] as Int
		binding.point.setColorFilter(color)
		binding.imageView.setColorFilter(color)
		val startTimeArray = context.resources.getStringArray(R.array.start_time)
		val endTimeArray = context.resources.getStringArray(R.array.end_time)
		val courseTimeText = "${startTimeArray[data.start - 1]} - ${endTimeArray[data.start + data.step - 2]}"
		binding.textViewCourseTime.text = courseTimeText
		val time = "${data.start} - ${data.start + data.step - 1}节"
		binding.textViewTime.text = time
	}
}