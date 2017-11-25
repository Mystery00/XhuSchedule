package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.listener.WeekChangeListener

/**
 * Created by myste.
 */
class WeekAdapter(private val context: Context,
                  private var index: Int) : RecyclerView.Adapter<WeekAdapter.ViewHolder>() {
    private var weekChangeListener: WeekChangeListener? = null

    override fun getItemCount(): Int = 20

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder.itemView as TextView).text = context.getString(R.string.course_week_index, position + 1)
        holder.itemView.setOnClickListener {
            weekChangeListener?.onChange(position)
        }
        val gradientDrawable=holder.itemView.background as GradientDrawable
        if (position + 1 == index)
            gradientDrawable.setColor(ContextCompat.getColor(context, R.color.colorWeekAccent))
        else
            gradientDrawable.setColor(ContextCompat.getColor(context, R.color.colorWeekPrimary))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(View.inflate(context, R.layout.item_week, null))

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun setWeekIndex(weekIndex: Int) {
        this.index = weekIndex
        notifyDataSetChanged()
    }

    fun setWeekChangeListener(weekChangeListener: WeekChangeListener) {
        this.weekChangeListener = weekChangeListener
    }
}