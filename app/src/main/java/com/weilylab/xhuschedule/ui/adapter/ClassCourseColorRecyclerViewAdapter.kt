/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.adapter

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemClassCourseColorBinding
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.event.UI
import com.weilylab.xhuschedule.model.event.UIConfigEvent
import com.weilylab.xhuschedule.utils.ConfigUtil
import com.weilylab.xhuschedule.viewmodel.ClassCourseColorViewModel
import org.greenrobot.eventbus.EventBus
import vip.mystery0.tools.base.binding.BaseBindingRecyclerViewAdapter

class ClassCourseColorRecyclerViewAdapter(private val context: Context,
                                          private val classCourseColorViewModel: ClassCourseColorViewModel,
                                          private val eventBus: EventBus) : BaseBindingRecyclerViewAdapter<Course, ItemClassCourseColorBinding>(R.layout.item_class_course_color) {
    override fun setItemView(binding: ItemClassCourseColorBinding, position: Int, data: Course) {
        binding.textView.text = data.name
        binding.imageView.setColorFilter(data.schedule.extras["colorInt"] as Int)
        binding.imageView.setOnClickListener {
            val colorPickerDialog = ColorPickerDialog.newBuilder()
                    .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                    .setColor(data.schedule.extras["colorInt"] as Int)
                    .setShowAlphaSlider(false)
                    .setShowColorShades(false)
                    .create()
            colorPickerDialog.setColorPickerDialogListener(object : ColorPickerDialogListener {
                override fun onDialogDismissed(dialogId: Int) {
                }

                override fun onColorSelected(dialogId: Int, color: Int) {
                    val newColor = ConfigUtil.toHexEncoding(color)
                    data.color = newColor
                    notifyItemChanged(position)
                    classCourseColorViewModel.updateCourseColor(data, newColor)
                    eventBus.post(UIConfigEvent(arrayListOf(UI.MAIN_INIT)))
                }
            })
            colorPickerDialog.show((context as FragmentActivity).supportFragmentManager, "color-picker-dialog")
        }
    }
}