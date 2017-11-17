package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.activity.MainActivity
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.listener.InfoChangeListener
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.ViewUtil
import vip.mystery0.tools.logs.Logs

/**
 * Created by myste.
 */
class TableAdapter(private val context: Context,
				   private val list: ArrayList<Course?>) : RecyclerView.Adapter<TableAdapter.ViewHolder>()
{
	companion object
	{
		private val TAG = "TableAdapter"
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int)
	{
		holder.linearLayout.removeAllViews()
		val course: Course? = list[position] ?: return
		addView(holder, course!!)
		var temp = course.other
		while (temp != null)
		{
			addView(holder, course.other!!)
			if (temp.other != null)
				temp = temp.other
			else
				break
		}
	}

	private fun addView(holder: ViewHolder, course: Course)
	{
		val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1F)
		val view = View.inflate(context, R.layout.item_course, null)
		val textViewName: TextView = view.findViewById(R.id.textView_name)
		val textViewTeacher: TextView = view.findViewById(R.id.textView_teacher)
		val textViewLocation: TextView = view.findViewById(R.id.textView_location)
		textViewName.text = course.name
		textViewTeacher.text = course.teacher
		textViewLocation.text = course.location
		if (course.transparencyColor == "")
		{
			course.transparencyColor = "#33" + ScheduleHelper.getInstance().getRandomColor()
		}
		view.setBackgroundColor(Color.parseColor(course.transparencyColor))
		view.layoutParams = layoutParams
		view.setOnClickListener {
			ViewUtil.showAlertDialog(context, course, object : InfoChangeListener
			{
				override fun onChange()
				{
					(context as MainActivity).updateView()
				}
			})
		}
		holder.linearLayout.addView(view)
	}

	override fun onCreateViewHolder(parent: ViewGroup?,
									viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_linear_layout, parent, false))

	override fun getItemCount(): Int = list.size

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
	{
		var linearLayout: LinearLayout = itemView.findViewById(R.id.linearLayout)
	}
}