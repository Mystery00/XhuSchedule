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
import com.weilylab.xhuschedule.databinding.ItemQueryExpScoreBinding
import com.weilylab.xhuschedule.model.ExpScore
import vip.mystery0.tools.base.binding.BaseBindingRecyclerViewAdapter

class QueryExpScoreRecyclerViewAdapter(val context: Context) : BaseBindingRecyclerViewAdapter<ExpScore, ItemQueryExpScoreBinding>(R.layout.item_query_exp_score) {
    override fun setItemView(binding: ItemQueryExpScoreBinding, position: Int, data: ExpScore) {
        binding.expScore = data
    }
}