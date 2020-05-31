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
import com.weilylab.xhuschedule.databinding.ItemQueryTestBinding
import com.weilylab.xhuschedule.model.Test
import vip.mystery0.tools.base.binding.BaseBindingRecyclerViewAdapter

class QueryTestRecyclerViewAdapter(val context: Context) : BaseBindingRecyclerViewAdapter<Test, ItemQueryTestBinding>(R.layout.item_query_test) {
	override fun setItemView(binding: ItemQueryTestBinding, position: Int, data: Test) {
		binding.test = data
		binding.textViewTestDate.text = context.getString(R.string.hint_query_test_date, data.date)
		binding.textViewTestTime.text = context.getString(R.string.hint_query_test_time, data.time)
		binding.textViewTestLocation.text = context.getString(R.string.hint_query_test_location, data.location)
		binding.textViewTestNo.text = context.getString(R.string.hint_query_test_test_no, data.testno)
		binding.textViewTestClassNo.text = context.getString(R.string.hint_query_test_test_class_no, data.no)
	}
}