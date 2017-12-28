/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-21 上午1:38
 */

package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Score
import vip.mystery0.tools.flexibleCardView.FlexibleCardView
import vip.mystery0.tools.logs.Logs

class ScoreAdapter(private val context: Context,
                   private val list: ArrayList<Score>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val score = list[position]
                holder.scoreNameTextView.text = score.name
                holder.scoreScoreTextView.text = score.score
                holder.scoreNoTextView.text = context.getString(R.string.score_no, score.no)
                holder.scoreCourseTypeTextView.text = context.getString(R.string.score_coursetype, score.coursetype)
                holder.scoreCreditTextView.text = context.getString(R.string.score_credit, score.credit)
                holder.scoreGpaTextView.text = context.getString(R.string.score_gpa, score.gpa)
                holder.flexibleCardView.setShowState(score.isExpand)
                holder.flexibleCardView.setOnClickListener {
                    holder.flexibleCardView.showAnime({ isExpand ->
                        score.isExpand = isExpand
                    })
                }
            }
            is EmptyViewHolder -> {
                holder.textView.text = context.getString(R.string.hint_score_fail)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> EmptyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_course_empty, parent, false))
            else -> ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_score, parent, false))
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return when {
            list.size == 1 -> 1
            list[position].name == "" -> 2
            else -> 0
        }
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.textView)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var flexibleCardView = itemView as FlexibleCardView
        var scoreNameTextView: TextView = itemView.findViewById(R.id.textView_score_name)
        var scoreScoreTextView: TextView = itemView.findViewById(R.id.textView_score_score)
        var scoreNoTextView: TextView = itemView.findViewById(R.id.textView_score_no)
        var scoreCourseTypeTextView: TextView = itemView.findViewById(R.id.textView_score_coursetype)
        var scoreCreditTextView: TextView = itemView.findViewById(R.id.textView_score_credit)
        var scoreGpaTextView: TextView = itemView.findViewById(R.id.textView_score_gpa)
    }
}