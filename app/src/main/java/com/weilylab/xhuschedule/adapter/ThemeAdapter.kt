/*
 * Created by Mystery0 on 18-3-11 上午10:43.
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
 * Last modified 18-3-11 上午10:43
 */

package com.weilylab.xhuschedule.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.Theme
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.ViewUtil

class ThemeAdapter(private val context: Context,
				   private val list: ArrayList<Theme>) : RecyclerView.Adapter<ThemeAdapter.ViewHolder>() {

	private var listener: ThemeApplyListener? = null

	fun setThemtListener(listener: (Theme, Int) -> Unit) {
		this.listener = object : ThemeApplyListener {
			override fun onSelect(theme: Theme, index: Int) {
				listener(theme, index)
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_theme, parent, false))
	}

	override fun getItemCount(): Int {
		return list.size
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val theme = list[position]
		val pointDrawable = VectorDrawableCompat.create(context.resources, R.drawable.ic_point, null)!!
		pointDrawable.setBounds(0, 0, pointDrawable.minimumWidth, pointDrawable.minimumHeight)
		pointDrawable.setTint(Color.parseColor(theme.primaryColor))
		holder.imageView.setImageDrawable(pointDrawable)
		holder.textView.text = theme.name
		if (theme.downloadLink == Settings.currentTheme) {
			holder.button.text = context.getString(R.string.action_use_theme)
			holder.button.background = BitmapDrawable(context.resources, ViewUtil.drawButtonDrawable(Color.parseColor(theme.primaryColor)))
			holder.button.setTextColor(Color.parseColor(theme.primaryColor))
		} else {
			holder.button.text = context.getString(R.string.action_theme)
			holder.button.background = BitmapDrawable(context.resources, ViewUtil.drawButtonDrawable(ContextCompat.getColor(context, R.color.not_theme_color)))
			holder.button.setTextColor(ContextCompat.getColor(context, R.color.not_theme_color))
		}
	}

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var imageView = itemView.findViewById<ImageView>(R.id.imageView)
		var textView = itemView.findViewById<TextView>(R.id.textView)
		var button = itemView.findViewById<Button>(R.id.button)
	}

	interface ThemeApplyListener {
		fun onSelect(theme: Theme, index: Int)
	}
}