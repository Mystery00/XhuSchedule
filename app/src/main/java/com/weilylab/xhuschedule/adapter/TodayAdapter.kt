/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-18 上午1:43
 */

package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.graphics.Color
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

/**
 * Created by myste.
 */
class TodayAdapter(private val context: Context,
                   private val list: ArrayList<Course>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int = if (list.size != 0) list.size else 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EmptyViewHolder -> {
                holder.textView.text = context.getString(R.string.hint_course_empty)
            }
            is ViewHolder -> {
                val course = list[position]
                holder.courseTimeTextView.text = course.time
                val temp = course.name + " - " + course.teacher
                holder.courseNameAndTeacherTextView.text = temp
                holder.courseLocationTextView.text = course.location
                holder.courseTimeTextView.setTextColor(Settings.customTodayTextColor)
                holder.courseNameAndTeacherTextView.setTextColor(Settings.customTodayTextColor)
                holder.courseLocationTextView.setTextColor(Settings.customTodayTextColor)
                if (course.color == "")
                    course.color = '#' + ScheduleHelper.getRandomColor()
                holder.img.setImageBitmap(ViewUtil.drawImg(course))
                holder.itemView.setOnClickListener {
                    ViewUtil.showAlertDialog(context, course, object : InfoChangeListener {
                        override fun onChange() {
                            (context as MainActivity).updateAllView()
                        }
                    })
                }
                holder.itemView.background.alpha = Settings.customTableOpacity
            }
        }
    }

    override fun getItemViewType(position: Int): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course_empty, parent, false)
            return EmptyViewHolder(view)
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course_today, parent, false)
        return ViewHolder(view)
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.textView)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img: ImageView = itemView.findViewById(R.id.img)
        var courseTimeTextView: TextView = itemView.findViewById(R.id.courseTimeTextView)
        var courseNameAndTeacherTextView: TextView = itemView.findViewById(R.id.courseNameAndTeacherTextView)
        var courseLocationTextView: TextView = itemView.findViewById(R.id.courseLocationTextView)
    }
}