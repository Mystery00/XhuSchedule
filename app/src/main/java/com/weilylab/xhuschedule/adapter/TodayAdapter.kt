package com.weilylab.xhuschedule.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Course
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by myste.
 */
class TodayAdapter(
		private val list: ArrayList<Course>) : RecyclerView.Adapter<TodayAdapter.ViewHolder>()
{
	override fun getItemCount(): Int
	{
		return list.size
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		val course = list[position]
		holder.courseTimeTextView.text = course.time
		val temp = course.name + " - " + course.teacher
		holder.courseNameAndTeacherTextView.text = temp
		holder.courseLocationTextView.text = course.location
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
	{
		val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course_today, parent, false)
		return ViewHolder(view)
	}

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
	{
		var img: CircleImageView = itemView.findViewById(R.id.img)
		var courseTimeTextView: TextView = itemView.findViewById(R.id.courseTimeTextView)
		var courseNameAndTeacherTextView: TextView = itemView.findViewById(R.id.courseNameAndTeacherTextView)
		var courseLocationTextView: TextView = itemView.findViewById(R.id.courseLocationTextView)
	}
}