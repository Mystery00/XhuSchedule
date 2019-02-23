package com.weilylab.xhuschedule.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.config.ColorPoolHelper
import com.weilylab.xhuschedule.databinding.ItemListCustomCourseWeekBinding
import vip.mystery0.tools.base.binding.BaseBindingRecyclerViewAdapter

class CustomCourseWeekAdapter(private val context: Context) : BaseBindingRecyclerViewAdapter<String, ItemListCustomCourseWeekBinding>(R.layout.item_list_custom_course_week) {
	val selectedList = ArrayList<String>()

	override fun setItemView(binding: ItemListCustomCourseWeekBinding, position: Int, data: String) {
		binding.index = data
		if (selectedList.contains(data)) {
			binding.root.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent))
			(binding.root as TextView).setTextColor(Color.WHITE)
		} else {
			binding.root.backgroundTintList = ColorStateList.valueOf(ColorPoolHelper.colorPool.uselessColor)
			(binding.root as TextView).setTextColor(Color.BLACK)
		}
		binding.root.setOnClickListener {
			if (selectedList.contains(data))
				selectedList.remove(data)
			else
				selectedList.add(data)
			notifyItemChanged(position)
		}
	}

	override fun onBindViewHolder(holder: BaseBindingViewHolder, position: Int) {
		val binding = DataBindingUtil.getBinding<ItemListCustomCourseWeekBinding>(holder.itemView)!!
		setItemView(binding, position, (position + 1).toString())
	}

	override fun getItemCount(): Int {
		return 20
	}
}