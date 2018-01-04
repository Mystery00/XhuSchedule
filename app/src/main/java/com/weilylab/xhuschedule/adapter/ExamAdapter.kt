/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-3 下午2:04
 */

package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.Exam
import vip.mystery0.tools.flexibleCardView.FlexibleCardView

class ExamAdapter(private val context: Context,
                  private val list: ArrayList<Exam>) : RecyclerView.Adapter<ExamAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exam = list[position]
        holder.examNameTextView.text = exam.name
        holder.examDateTextView.text = exam.date
        holder.examTestNoTextView.text = exam.testno
        holder.examNoTextView.text = context.getString(R.string.exam_no, exam.no)
        holder.examSnameTextView.text = context.getString(R.string.exam_sname, exam.sname)
        holder.examLocationTextView.text = context.getString(R.string.exam_location, exam.location)
        holder.examTimeTextView.text = context.getString(R.string.exam_time, exam.time)
        holder.examTestTypeTextView.text = context.getString(R.string.exam_testtype, exam.testtype)
        holder.examRegionTextView.text = context.getString(R.string.exam_region, exam.region)
        holder.flexibleCardView.setShowState(exam.isExpand)
        holder.flexibleCardView.setOnClickListener {
            holder.flexibleCardView.showAnime({ isExpand ->
                exam.isExpand = isExpand
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_exam, parent, false))
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var flexibleCardView = itemView as FlexibleCardView
        var examNameTextView: TextView = itemView.findViewById(R.id.textView_exam_name)
        var examDateTextView: TextView = itemView.findViewById(R.id.textView_exam_date)
        var examNoTextView: TextView = itemView.findViewById(R.id.textView_exam_no)
        var examSnameTextView: TextView = itemView.findViewById(R.id.textView_exam_sname)
        var examLocationTextView: TextView = itemView.findViewById(R.id.textView_exam_location)
        var examTimeTextView: TextView = itemView.findViewById(R.id.textView_exam_time)
        var examTestNoTextView: TextView = itemView.findViewById(R.id.textView_exam_testno)
        var examTestTypeTextView: TextView = itemView.findViewById(R.id.textView_exam_testtype)
        var examRegionTextView: TextView = itemView.findViewById(R.id.textView_exam_region)
    }
}