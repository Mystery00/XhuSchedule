/*
 * Created by Mystery0 on 17-12-1 下午1:52.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-1 下午1:52
 */

package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Profile

/**
 * Created by mystery0.
 */
class ProfileAdapter(private val context: Context,
                     private val list: ArrayList<Profile>) : RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {
    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = View.inflate(context, R.layout.item_profile, parent)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val profile = list[position]
        val text = "${profile.no}(${profile.name})"
        holder.header.text = text
        holder.sex.text = profile.sex
        holder.institute.text = profile.institute
        holder.professional.text = profile.profession
        holder.classname.text = profile.classname
        holder.grade.text = profile.grade
        holder.direction.text = profile.direction
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var header: TextView = itemView.findViewById(R.id.textView_student_header)
        var sex: TextView = itemView.findViewById(R.id.textView_student_sex)
        var institute: TextView = itemView.findViewById(R.id.textView_student_institute)
        var professional: TextView = itemView.findViewById(R.id.textView_student_professional)
        var classname: TextView = itemView.findViewById(R.id.textView_student_classname)
        var grade: TextView = itemView.findViewById(R.id.textView_student_grade)
        var direction: TextView = itemView.findViewById(R.id.textView_student_direction)
    }
}