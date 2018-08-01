package com.weilylab.xhuschedule.newPackage.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemFragmentTodayBinding
import com.weilylab.xhuschedule.newPackage.model.Course
import com.zhuangfei.timetable.model.Schedule
import com.zhuangfei.timetable.model.ScheduleColorPool
import vip.mystery0.tools.base.BaseRecyclerViewAdapter

class FragmentTodayRecyclerViewAdapter(private val context: Context?, list: ArrayList<in Schedule>) : BaseRecyclerViewAdapter<FragmentTodayRecyclerViewAdapter.ViewHolder, Schedule>(context, R.layout.item_fragment_today, list) {
	private lateinit var itemFragmentTodayBinding: ItemFragmentTodayBinding
	private var listener: (Int, Schedule) -> Boolean = { _, _ -> false }

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		itemFragmentTodayBinding = ItemFragmentTodayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ViewHolder(itemFragmentTodayBinding)
	}

	override fun setItemView(holder: ViewHolder, position: Int, data: Schedule) {
		itemFragmentTodayBinding.course = data
		val color = ScheduleColorPool(context).getColorAuto(data.colorRandom)
		itemFragmentTodayBinding.point.setColorFilter(color)
		itemFragmentTodayBinding.imageView.setColorFilter(color)
		val time = "${data.start}-${data.start + data.step - 1}节"
		itemFragmentTodayBinding.textViewTime.text = time
		itemFragmentTodayBinding.root.setOnClickListener {
			listener.invoke(position, data)
		}
	}

	fun setOnItemClickListener(listener: (Int, Schedule) -> Boolean) {
		this.listener = listener
	}

	inner class ViewHolder(itemFragmentTodayBinding: ItemFragmentTodayBinding) : RecyclerView.ViewHolder(itemFragmentTodayBinding.root)
}