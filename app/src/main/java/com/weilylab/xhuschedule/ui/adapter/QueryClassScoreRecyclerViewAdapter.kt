package com.weilylab.xhuschedule.ui.adapter

import android.content.Context
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemQueryClassScoreBinding
import com.weilylab.xhuschedule.model.ClassScore
import vip.mystery0.tools.base.BaseBindingRecyclerViewAdapter

class QueryClassScoreRecyclerViewAdapter(val context: Context) : BaseBindingRecyclerViewAdapter<ClassScore, ItemQueryClassScoreBinding>(R.layout.item_query_class_score) {
	override fun setItemView(binding: ItemQueryClassScoreBinding, position: Int, data: ClassScore) {
		binding.classScore = data
	}
}