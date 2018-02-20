/*
 * Created by Mystery0 on 18-2-20 下午1:47.
 * Copyright (c) 2018. All Rights reserved.
 *
 * Last modified 18-2-20 下午1:47
 */

package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.weilylab.xhuschedule.R

class ShareWithFriendsAdapter(private val context: Context) : RecyclerView.Adapter<ShareWithFriendsAdapter.ViewHolder>() {
    private val list = ArrayList<HashMap<String, Int>>()

    init {
        val titleArray = arrayOf(
                R.string.operation_notice,
                R.string.operation_schedule,
                R.string.operation_exam,
                R.string.operation_score,
                R.string.operation_feedback,
                R.string.operation_share,
                R.string.operation_settings
        )
        val imgArray = arrayOf(
                R.drawable.ic_notice,
                R.drawable.ic_schedule,
                R.drawable.ic_exam,
                R.drawable.ic_score,
                R.drawable.ic_feedback,
                R.drawable.ic_share_app,
                R.drawable.ic_settings
        )
        for (i in 0 until titleArray.size) {
            val map = HashMap<String, Int>()
            map["title"] = titleArray[i]
            map["icon"] = imgArray[i]
            list.add(map)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val map = list[position]
        holder.imageView.setImageResource(map["icon"]!!)
        holder.textView.setText(map["title"]!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_share, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
        var textView: TextView = itemView.findViewById(R.id.textView)
    }
}