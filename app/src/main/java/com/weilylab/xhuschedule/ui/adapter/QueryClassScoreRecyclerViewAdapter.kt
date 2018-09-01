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
		val gpaString = "${data.gpa}/"
		binding.textViewGpa.text = gpaString
		binding.textViewGpa.visibility = if (ConfigurationUtil.isShowGpa) View.VISIBLE else View.GONE
	}
}