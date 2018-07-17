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
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemWeekBinding

/**
 * Created by myste.
 */
class WeekAdapter(private val context: Context,
				  private var index: Int) : RecyclerView.Adapter<WeekAdapter.ViewHolder>() {
	private var weekChangeListener: ((Int) -> Unit)? = null

	override fun getItemCount(): Int = 20

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.binding.week = context.getString(R.string.course_week_index, position + 1)
		holder.binding.weekTextView.setOnClickListener {
			weekChangeListener?.invoke(position)
		}
		val gradientDrawable = holder.binding.weekTextView.background as GradientDrawable
		if (position + 1 == index)
			gradientDrawable.setColor(ContextCompat.getColor(context, R.color.colorWeekPrimary))
		else
			gradientDrawable.setColor(ContextCompat.getColor(context, R.color.colorWeekAccent))
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(ItemWeekBinding.inflate(LayoutInflater.from(context)))

	class ViewHolder(val binding: ItemWeekBinding) : RecyclerView.ViewHolder(binding.root)

	fun setWeekIndex(weekIndex: Int) {
		index = weekIndex
		notifyDataSetChanged()
	}

	fun setWeekChangeListener(listener: (Int) -> Unit) {
		weekChangeListener = listener
	}
}