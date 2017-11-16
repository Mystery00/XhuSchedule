package com.weilylab.xhuschedule.util

import android.content.Context
import android.graphics.Color
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ColorPickerAdapter
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.listener.ColorPickerChangeListener

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
		val textView: TextView = view.findViewById(R.id.titleTextView)
		val editTeacherLayout: TextView = view.findViewById(R.id.edit_teacher_layout)
		val editTimeLayout: LinearLayout = view.findViewById(R.id.edit_time_layout)
		val editColorTag: TextView = view.findViewById(R.id.edit_color_tag)
		val colorChooser: RecyclerView = view.findViewById(R.id.color_chooser)
		colorChooser.layoutManager = GridLayoutManager(context, 6)
		val adapter = ColorPickerAdapter(course.color, context)
		adapter.colorPickerChangeListener = object : ColorPickerChangeListener
		{
			override fun onColorChanged(color: String)
			{
				textView.setBackgroundColor(Color.parseColor(color))
				changeTextColor(Color.parseColor(color), textView)
			}
		}
		colorChooser.adapter = adapter
		textView.text = course.name
		textView.setBackgroundColor(Color.parseColor(course.color))
		changeTextColor(Color.parseColor(course.color), textView)
		editTeacherLayout.text = course.teacher
		val dialog = AlertDialog.Builder(context)
				.setView(view)
				.create()
		dialog.show()
	}

	private fun changeTextColor(color: Int, textView: TextView)
	{
		val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255F
		if (darkness < 0.5)
			textView.setTextColor(Color.BLACK)
		else
			textView.setTextColor(Color.WHITE)
	}
}