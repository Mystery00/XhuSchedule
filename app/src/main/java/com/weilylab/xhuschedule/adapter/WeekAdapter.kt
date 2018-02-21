/*
 * Created by Mystery0 on 18-2-21 下午9:12.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 18-2-21 下午9:11
 */

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
        holder.textView.text = context.getString(R.string.course_week_index, position + 1)
        holder.textView.setOnClickListener {
            weekChangeListener?.onChange(position)
        }
        val gradientDrawable = holder.textView.background as GradientDrawable
        if (position + 1 == index)
            gradientDrawable.setColor(ContextCompat.getColor(context, R.color.colorWeekPrimary))
        else
            gradientDrawable.setColor(ContextCompat.getColor(context, R.color.colorWeekAccent))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(View.inflate(context, R.layout.item_week, null))

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var textView:TextView=itemView.findViewById(R.id.weekTextView)
    }

    fun setWeekIndex(weekIndex: Int) {
        this.index = weekIndex
        notifyDataSetChanged()
    }

    fun setWeekChangeListener(weekChangeListener: WeekChangeListener) {
        this.weekChangeListener = weekChangeListener
    }
}