/*
 * Created by Mystery0 on 17-12-20 下午8:51.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-20 下午8:51
 */

package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.weilylab.xhuschedule.R

class HeaderAdapter(private val context: Context) : RecyclerView.Adapter<HeaderAdapter.ViewHolder>() {
    private val imgArray = context.resources.getStringArray(R.array.header_img)
    var listener: ItemSelectedListener? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(imgArray[position]).into((holder.itemView as ImageView))
        holder.itemView.setOnClickListener {
            listener?.onChecked(imgArray[holder.adapterPosition], holder.adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.dialog_choose_img_header, parent, false))
    }

    override fun getItemCount(): Int = imgArray.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    interface ItemSelectedListener {
        fun onChecked(link: String, position: Int)
    }
}