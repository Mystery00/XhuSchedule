/*
 * Created by Mystery0 on 17-12-1 下午9:57.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-1 下午9:57
 */

package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.weilylab.xhuschedule.R

class OperationAdapter(private val context: Context) : RecyclerView.Adapter<OperationAdapter.ViewHolder>() {
    private val list = ArrayList<HashMap<String, Int>>()

    init {
        for (i in 0 until 6) {
            val map = HashMap<String, Int>()
            map.put("title", R.string.action_settings)
            map.put("icon", R.drawable.ic_week)
            list.add(map)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val map = list[position]
        holder.imageView.setImageResource(map["icon"]!!)
        holder.textView.setText(map["title"]!!)
        holder.itemView.setOnClickListener {
            Toast.makeText(context, position.toString(), Toast.LENGTH_SHORT)
                    .show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_operation, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
        var textView: TextView = itemView.findViewById(R.id.textView)
    }
}