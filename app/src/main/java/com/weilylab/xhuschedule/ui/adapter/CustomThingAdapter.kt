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

package com.weilylab.xhuschedule.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemListCustomThingBinding
import com.weilylab.xhuschedule.model.CustomThing
import vip.mystery0.tools.base.binding.BaseBindingRecyclerViewAdapter

class CustomThingAdapter(private val context: Context) : BaseBindingRecyclerViewAdapter<CustomThing, ItemListCustomThingBinding>(R.layout.item_list_custom_thing) {
	override fun setItemView(binding: ItemListCustomThingBinding, position: Int, data: CustomThing) {
		binding.customThing = data
		binding.root.backgroundTintList = ColorStateList.valueOf(Color.parseColor(data.color))
		val text = "${data.startTime} - ${data.endTime}"
		binding.textViewTime.text = text
	}
}