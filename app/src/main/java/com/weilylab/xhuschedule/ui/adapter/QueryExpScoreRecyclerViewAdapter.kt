package com.weilylab.xhuschedule.ui.adapter

import android.content.Context
import android.view.View
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemQueryClassScoreBinding
import com.weilylab.xhuschedule.databinding.ItemQueryExpScoreBinding
import com.weilylab.xhuschedule.model.ClassScore
import com.weilylab.xhuschedule.model.ExpScore
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import vip.mystery0.tools.base.BaseBindingRecyclerViewAdapter

class QueryExpScoreRecyclerViewAdapter(val context: Context) : BaseBindingRecyclerViewAdapter<ExpScore, ItemQueryExpScoreBinding>(R.layout.item_query_exp_score) {
	override fun setItemView(binding: ItemQueryExpScoreBinding, position: Int, data: ExpScore) {
		binding.expScore = data
	}
}