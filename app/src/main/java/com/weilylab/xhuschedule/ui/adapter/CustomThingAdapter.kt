/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
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
	private var clickListener: ((CustomThing) -> Unit)? = null

	override fun setItemView(binding: ItemListCustomThingBinding, position: Int, data: CustomThing) {
		binding.customThing = data
		binding.root.backgroundTintList = ColorStateList.valueOf(Color.parseColor(data.color))
		val text = "${data.startTime} - ${data.endTime}"
		binding.textViewTime.text = text
		binding.root.setOnClickListener { clickListener?.invoke(data) }
	}

	fun setOnClickListener(listener: (CustomThing) -> Unit) {
		this.clickListener = listener
	}
}