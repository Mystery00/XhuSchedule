package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.graphics.*
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.activity.MainActivity
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.listener.InfoChangeListener
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.ViewUtil
import vip.mystery0.tools.logs.Logs

/**
 * Created by myste.
 */
class TodayAdapter(private val context: Context,
                   private val list: ArrayList<Course>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private val TAG = "TodayAdapter"
    }

    override fun getItemCount(): Int = if (list.size != 0) list.size else 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EmptyViewHolder -> {
                val textView = holder.itemView as TextView
                textView.text = context.getString(R.string.hint_course_empty)
            }
            is ViewHolder -> {
                val course = list[position]
                holder.courseTimeTextView.text = course.time
                val temp = course.name + " - " + course.teacher
                holder.courseNameAndTeacherTextView.text = temp
                holder.courseLocationTextView.text = course.location
                if (course.color == "")
                    course.color = '#' + ScheduleHelper.getRandomColor()
                holder.img.setImageBitmap(drawImg(course))
                holder.itemView.setOnClickListener {
                    ViewUtil.showAlertDialog(context, course, object : InfoChangeListener {
                        override fun onChange() {
                            (context as MainActivity).updateView()
                        }
                    })
                }
                holder.itemView.background.alpha = Settings.customTransparency
            }
        }
    }

    override fun getItemViewType(position: Int): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course_empty, parent, false).findViewById<TextView>(R.id.textView)
            return EmptyViewHolder(view)
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course_today, parent, false)
        return ViewHolder(view)
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img: ImageView = itemView.findViewById(R.id.img)
        var courseTimeTextView: TextView = itemView.findViewById(R.id.courseTimeTextView)
        var courseNameAndTeacherTextView: TextView = itemView.findViewById(R.id.courseNameAndTeacherTextView)
        var courseLocationTextView: TextView = itemView.findViewById(R.id.courseLocationTextView)
    }

    private fun drawImg(course: Course): Bitmap {
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val targetRect = Rect(0, 0, 200, 200)
        val paint = Paint()
        paint.color = Color.parseColor(course.color)
        canvas.drawCircle(100F, 100F, 100F, paint)
        paint.color = Color.WHITE
        paint.textSize = 120F
        val fontMetrics = paint.fontMetrics
        val baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(course.name.substring(0, 1), targetRect.centerX().toFloat(), baseline, paint)
        return bitmap
    }
}