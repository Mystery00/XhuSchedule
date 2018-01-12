/*
 * Created by Mystery0 on 18-1-12 下午9:06.
 * Copyright (c) 2018. All Rights reserved.
 *
 * Last modified 18-1-12 下午9:06
 */

package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.Notice

class NoticeAdapter(private val context: Context, private val list: ArrayList<Notice>) : RecyclerView.Adapter<NoticeAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notice = list[position]
        holder.noticeTitle.text = notice.title
        holder.noticeContent.text = notice.content
        holder.noticeDate.text = notice.createTime
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_notice, parent, false))

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var noticeTitle: TextView = itemView.findViewById(R.id.notice_title)
        var noticeContent: TextView = itemView.findViewById(R.id.notice_content)
        var noticeDate: TextView = itemView.findViewById(R.id.notice_date)
    }
}