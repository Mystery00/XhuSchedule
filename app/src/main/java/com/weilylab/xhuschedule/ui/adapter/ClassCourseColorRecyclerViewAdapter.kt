package com.weilylab.xhuschedule.ui.adapter

import android.app.Activity
import android.content.Context
import com.jrummyapps.android.colorpicker.ColorPickerDialog
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemClassCourseColorBinding
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.repository.local.CourseLocalDataSource
import com.weilylab.xhuschedule.utils.ConfigUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import vip.mystery0.logs.Logs
import vip.mystery0.tools.base.binding.BaseBindingRecyclerViewAdapter

class ClassCourseColorRecyclerViewAdapter(private val context: Context) : BaseBindingRecyclerViewAdapter<Course, ItemClassCourseColorBinding>(R.layout.item_class_course_color) {
	override fun setItemView(binding: ItemClassCourseColorBinding, position: Int, data: Course) {
		binding.textView.text = data.name
		binding.imageView.setBackgroundColor(data.schedule.extras["colorInt"] as Int)
		binding.imageView.setOnClickListener {
			val colorPickerDialog = ColorPickerDialog.newBuilder()
					.setDialogType(ColorPickerDialog.TYPE_PRESETS)
					.setColor(data.schedule.extras["colorInt"] as Int)
					.setShowAlphaSlider(false)
					.create()
			colorPickerDialog.setColorPickerDialogListener(object : ColorPickerDialogListener {
				override fun onDialogDismissed(dialogId: Int) {
				}

				override fun onColorSelected(dialogId: Int, color: Int) {
					data.color = ConfigUtil.toHexEncoding(color)
					Logs.i("onColorSelected: ${data.color}")
					notifyItemChanged(position)
					CourseLocalDataSource.updateCourseColor(data)
					LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity = true
				}
			})
			colorPickerDialog.show((context as Activity).fragmentManager, "color-picker-dialog")
		}
	}
}