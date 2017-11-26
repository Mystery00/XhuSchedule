package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.activity.MainActivity
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.listener.InfoChangeListener
import com.weilylab.xhuschedule.util.DensityUtil
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.ViewUtil
import java.util.*

/**
 * Created by myste.
 */
class TableAdapter(private val context: Context,
                   private val list: ArrayList<LinkedList<Course>>) : RecyclerView.Adapter<TableAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.linearLayout.removeAllViews()
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context, 144F))
        holder.linearLayout.layoutParams = layoutParams
        val linkedList=list[position]
        linkedList.forEach {
            addView(holder,it)
        }
    }

    private fun addView(holder: ViewHolder, course: Course) {
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1F)
        val view = View.inflate(context, R.layout.item_course, null)
        val courseBackground: View = view.findViewById(R.id.courseBackground)
        val textViewName: TextView = view.findViewById(R.id.textView_name)
        val textViewTeacher: TextView = view.findViewById(R.id.textView_teacher)
        val textViewLocation: TextView = view.findViewById(R.id.textView_location)
        val textSize = Settings.customTextSize
        textViewName.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        textViewTeacher.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        textViewLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        textViewName.text = course.name
        textViewTeacher.text = course.teacher
        textViewLocation.text = course.location
        textViewName.setTextColor(Settings.customTableTextColor)
        textViewTeacher.setTextColor(Settings.customTableTextColor)
        textViewLocation.setTextColor(Settings.customTableTextColor)

        if (course.color == "") {
            course.color = '#' + ScheduleHelper.getRandomColor()
        }
        val gradientDrawable = courseBackground.background as GradientDrawable
        gradientDrawable.setColor(Color.parseColor('#' + Integer.toHexString(Settings.customTableOpacity) + course.color.substring(1)))
        view.layoutParams = layoutParams
        view.setOnClickListener {
            ViewUtil.showAlertDialog(context, course, object : InfoChangeListener {
                override fun onChange() {
                    (context as MainActivity).updateAllView()
                }
            })
        }
        holder.linearLayout.addView(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup?,
                                    viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_linear_layout, parent, false))

    override fun getItemCount(): Int = list.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var linearLayout: LinearLayout = itemView.findViewById(R.id.linearLayout)
    }
}