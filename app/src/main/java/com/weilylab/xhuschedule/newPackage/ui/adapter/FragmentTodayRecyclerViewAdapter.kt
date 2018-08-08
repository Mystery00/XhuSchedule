package com.weilylab.xhuschedule.newPackage.ui.adapter

import android.content.Context
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemFragmentTodayBinding
import com.zhuangfei.timetable.model.Schedule
import com.zhuangfei.timetable.model.ScheduleColorPool
import vip.mystery0.tools.base.BaseBindingRecyclerViewAdapter

class FragmentTodayRecyclerViewAdapter(private val context: Context?) : BaseBindingRecyclerViewAdapter<Schedule,ItemFragmentTodayBinding>(R.layout.item_fragment_today) {
	private var listener: (Int, Schedule) -> Boolean = { _, _ -> false }

	override fun setItemView(binding: ItemFragmentTodayBinding, position: Int, data: Schedule) {
		binding.course = data
		val color = ScheduleColorPool(context).getColorAuto(data.colorRandom)
		binding.point.setColorFilter(color)
		binding.imageView.setColorFilter(color)
		val time = "${data.start}-${data.start + data.step - 1}节"
		binding.textViewTime.text = time
		binding.root.setOnClickListener {
			listener.invoke(position, data)
		}
	}

	fun setOnItemClickListener(listener: (Int, Schedule) -> Boolean) {
		this.listener = listener
	}
}