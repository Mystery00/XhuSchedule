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
import android.view.View
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemQueryClassScoreBinding
import com.weilylab.xhuschedule.model.ClassScore
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import vip.mystery0.tools.base.binding.BaseBindingRecyclerViewAdapter

class QueryClassScoreRecyclerViewAdapter(val context: Context) : BaseBindingRecyclerViewAdapter<ClassScore, ItemQueryClassScoreBinding>(R.layout.item_query_class_score) {
	override fun setItemView(binding: ItemQueryClassScoreBinding, position: Int, data: ClassScore) {
		binding.classScore = data
		val gpaString = "${if (data.gpa.isBlank()) "空" else data.gpa}/"
		binding.textViewGpa.text = gpaString
		val creditString = "${if (data.credit.isBlank()) "空" else data.credit}/"
		binding.textViewCredit.text = creditString
		binding.textViewGpa.visibility = if (ConfigurationUtil.isShowGpa) View.VISIBLE else View.GONE
		binding.textViewCredit.visibility = if (ConfigurationUtil.isShowCredit) View.VISIBLE else View.GONE
		binding.textViewCourseType.visibility = if (ConfigurationUtil.isShowCourseType && data.coursetype.isNotBlank()) View.VISIBLE else View.GONE
	}
}