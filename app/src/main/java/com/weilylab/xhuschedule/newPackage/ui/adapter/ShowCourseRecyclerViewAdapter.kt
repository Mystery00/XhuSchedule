package com.weilylab.xhuschedule.newPackage.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemShowCourseBinding
import com.zhuangfei.timetable.model.Schedule
import com.zhuangfei.timetable.model.ScheduleColorPool
import vip.mystery0.tools.base.BaseRecyclerViewAdapter

class ShowCourseRecyclerViewAdapter(private val context: Context?, list: ArrayList<in Schedule>) : BaseRecyclerViewAdapter<ShowCourseRecyclerViewAdapter.ViewHolder, Schedule>(context, R.layout.item_show_course, list) {
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(ItemShowCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false))

	override fun setItemView(holder: ViewHolder, position: Int, data: Schedule) {
		val stringBuilder = StringBuilder()
		stringBuilder.appendln(data.name)
		stringBuilder.appendln(data.teacher)
		stringBuilder.appendln(data.room)
		holder.itemShowCourseBinding.textView.text = stringBuilder.toString()
		val color = ScheduleColorPool(context).getColorAuto(data.colorRandom)
		holder.itemShowCourseBinding.cardView.setCardBackgroundColor(color)
	}

	inner class ViewHolder(val itemShowCourseBinding: ItemShowCourseBinding) : RecyclerView.ViewHolder(itemShowCourseBinding.root)
}