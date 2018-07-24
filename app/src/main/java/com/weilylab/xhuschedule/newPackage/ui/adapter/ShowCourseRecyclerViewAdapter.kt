package com.weilylab.xhuschedule.newPackage.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemShowCourseBinding
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.tools.base.BaseRecyclerViewAdapter

class ShowCourseRecyclerViewAdapter(context: Context?, list: ArrayList<in Schedule>) : BaseRecyclerViewAdapter<ShowCourseRecyclerViewAdapter.ViewHolder, Schedule>(context, R.layout.item_show_course, list) {
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(ItemShowCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false))

	override fun setItemView(holder: ViewHolder, position: Int, data: Schedule) {
		val stringBuilder = StringBuilder()
		stringBuilder.appendln(data.name)
		stringBuilder.appendln(data.teacher)
		stringBuilder.appendln(data.room)
		holder.itemShowCourseBinding.textView.text = stringBuilder.toString()
	}

	inner class ViewHolder(val itemShowCourseBinding: ItemShowCourseBinding) : RecyclerView.ViewHolder(itemShowCourseBinding.root)
}