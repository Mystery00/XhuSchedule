package com.weilylab.xhuschedule.util

import android.content.Context
import android.graphics.Color
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ColorPickerAdapter
import com.weilylab.xhuschedule.classes.Course
import vip.mystery0.tools.logs.Logs

/**
 * Created by myste.
 */
object ViewUtil
{
	private val TAG = "ViewUtil"

	@JvmStatic
	fun showAlertDialog(context: Context, course: Course)
	{
		val view = View.inflate(context, R.layout.dialog_edit, null)
		val edit_name_layout: TextView = view.findViewById(R.id.edit_name_layout)
		val edit_teacher_layout: TextView = view.findViewById(R.id.edit_teacher_layout)
		val edit_time_layout: LinearLayout = view.findViewById(R.id.edit_time_layout)
		val edit_color_tag: TextView = view.findViewById(R.id.edit_color_tag)
		val colorChooser: RecyclerView = view.findViewById(R.id.color_chooser)
		colorChooser.layoutManager = GridLayoutManager(context, 6)
		colorChooser.adapter = ColorPickerAdapter(context)
		val dialog = AlertDialog.Builder(context)
				.setView(view)
				.create()
		edit_name_layout.text = course.name
		edit_name_layout.setBackgroundColor(Color.parseColor(course.color))
		edit_teacher_layout.text = course.teacher
		dialog.show()
	}
}