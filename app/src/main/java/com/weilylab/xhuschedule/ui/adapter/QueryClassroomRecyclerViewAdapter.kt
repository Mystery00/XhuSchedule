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
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemQueryClassroomBinding
import com.weilylab.xhuschedule.model.Classroom
import vip.mystery0.tools.base.binding.BaseBindingRecyclerViewAdapter

class QueryClassroomRecyclerViewAdapter(val context: Context) : BaseBindingRecyclerViewAdapter<Classroom, ItemQueryClassroomBinding>(R.layout.item_query_classroom) {
	override fun setItemView(binding: ItemQueryClassroomBinding, position: Int, data: Classroom) {
		val noString = "教室编号：${data.no}"
		binding.textViewNo.text = noString
		val nameString = "教室名称：${data.name}"
		binding.textViewName.text = nameString
		val seatString = "座位数：${data.seat}"
		binding.textViewSeat.text = seatString
		val regionString = "所在地区：${data.region}"
		binding.textViewRegion.text = regionString
		val typeString = "教室类型：${data.type}"
		binding.textViewType.text = typeString
	}
}