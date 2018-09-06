package com.weilylab.xhuschedule.ui.adapter

import android.content.Context
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemClassCourseColorBinding
import com.weilylab.xhuschedule.model.Course
import vip.mystery0.tools.base.binding.BaseBindingRecyclerViewAdapter

class ClassCourseColorRecyclerViewAdapter(private val context: Context,
										  itemLayoutId: Int) : BaseBindingRecyclerViewAdapter<Course, ItemClassCourseColorBinding>(itemLayoutId) {
	override fun setItemView(binding: ItemClassCourseColorBinding, position: Int, data: Course) {
		binding.textView.text = data.name
		val grayPointDrawable = VectorDrawableCompat.create(context.resources, R.drawable.ic_point, null)!!
		grayPointDrawable.setBounds(0, 0, 20, 20)
		grayPointDrawable.setTint(data.schedule.extras["colorInt"] as Int)
		binding.imageView.background = grayPointDrawable
	}
}