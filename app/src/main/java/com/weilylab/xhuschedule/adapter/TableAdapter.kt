package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Course
import vip.mystery0.tools.logs.Logs

/**
 * Created by myste.
 */
class TableAdapter(private val context: Context,
				   private val list: ArrayList<Course>) : RecyclerView.Adapter<TableAdapter.ViewHolder>()
{
	companion object
	{
		private val TAG = "TableAdapter"
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		holder.linearLayout.removeAllViews()
		val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1F)
		var course = list[position]
		val view = LayoutInflater.from(context).inflate(R.layout.item_course, null)
		val textViewName: TextView = view.findViewById(R.id.textView_name)
		val textViewTeacher: TextView = view.findViewById(R.id.textView_teacher)
		val textViewLocation: TextView = view.findViewById(R.id.textView_location)
		textViewName.text = course.name
		textViewTeacher.text = course.teacher
		textViewLocation.text = course.location
		view.layoutParams = layoutParams
		holder.linearLayout.addView(view)
		while (course.other != null)
		{
			course = course.other!!
			val tempView = LayoutInflater.from(context).inflate(R.layout.item_course, null)
			val tempTextViewName: TextView = tempView.findViewById(R.id.textView_name)
			val tempTextViewTeacher: TextView = tempView.findViewById(R.id.textView_teacher)
			val tempTextViewLocation: TextView = tempView.findViewById(R.id.textView_location)
			tempTextViewName.text = course.name
			tempTextViewTeacher.text = course.teacher
			tempTextViewLocation.text = course.location
			tempView.layoutParams = layoutParams
			holder.linearLayout.addView(tempView)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder
	{
		return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_linear_layout, parent, false))
	}

	override fun getItemCount(): Int
	{
		return list.size
	}

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
	{
		var linearLayout: LinearLayout = itemView.findViewById(R.id.linearLayout)
	}
}