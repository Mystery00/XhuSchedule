package com.weilylab.xhuschedule.util

import android.content.Context
import android.graphics.Color
import android.support.design.widget.FloatingActionButton
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
import com.weilylab.xhuschedule.listener.InfoChangeListener

/**
 * Created by myste.
 */
object ViewUtil
{
	fun showAlertDialog(context: Context, course: Course, infoChangeListener: InfoChangeListener)
	{
		val view = View.inflate(context, R.layout.dialog_edit, null)
		val textView: TextView = view.findViewById(R.id.toolbar)
		val editTeacherLayout: TextView = view.findViewById(R.id.edit_teacher_layout)
		val editTimeLayout: TextView = view.findViewById(R.id.edit_time_layout)
		val editLocationLayout: LinearLayout = view.findViewById(R.id.edit_location_layout)
		val colorChooser: RecyclerView = view.findViewById(R.id.color_chooser)
		val floatingActionButton: FloatingActionButton = view.findViewById(R.id.floatingActionButton)
		colorChooser.layoutManager = GridLayoutManager(context, 6)
		val adapter = ColorPickerAdapter(course.color, context)
		adapter.colorPickerChangeListener = object : ColorPickerChangeListener
		{
			override fun onColorChanged(color: String)
			{
				textView.setBackgroundColor(Color.parseColor(color))
			}
		}
		colorChooser.adapter = adapter
		textView.text = course.name
		textView.setBackgroundColor(Color.parseColor(course.color))
		editTeacherLayout.text = course.teacher
		val startTime = context.resources.getStringArray(R.array.start_time)
		val endTime = context.resources.getStringArray(R.array.end_time)
		val time = course.time.trim().split("-")
		editTimeLayout.text = context.getString(R.string.course_time_format, startTime[time[0].toInt() - 1], endTime[time[1].toInt() - 1])
		CourseUtil.splitInfo(course).forEach {
			val child = TextView(context)
			child.text = context.getString(R.string.course_location_format, it.week, it.location)
			child.paint.isFakeBoldText = true
			editLocationLayout.addView(child)
		}
		val dialog = AlertDialog.Builder(context)
				.setView(view)
				.create()
		floatingActionButton.setOnClickListener {
			val colorSharedPreference = context.getSharedPreferences("course_color", Context.MODE_PRIVATE)
			val md5 = ScheduleHelper.getMD5(course.name)
			colorSharedPreference.edit()
					.putString(md5, adapter.color)
					.putString(md5 + "_trans", "#33" + adapter.color.substring(1))
					.apply()
			infoChangeListener.onChange()
			dialog.dismiss()
		}
		dialog.show()
	}
}