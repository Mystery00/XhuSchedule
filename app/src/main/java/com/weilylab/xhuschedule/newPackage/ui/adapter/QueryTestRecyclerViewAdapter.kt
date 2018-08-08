package com.weilylab.xhuschedule.newPackage.ui.adapter

import android.content.Context
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemQueryTestBinding
import com.weilylab.xhuschedule.newPackage.model.Test
import vip.mystery0.tools.base.BaseBindingRecyclerViewAdapter

class QueryTestRecyclerViewAdapter(val context: Context) : BaseBindingRecyclerViewAdapter<Test, ItemQueryTestBinding>(R.layout.item_query_test) {
	override fun setItemView(binding: ItemQueryTestBinding, position: Int, data: Test) {
		binding.test = data
		binding.textViewTestDate.text = context.getString(R.string.hint_query_test_date, data.date)
		binding.textViewTestTime.text = context.getString(R.string.hint_query_test_time, data.time)
		binding.textViewTestLocation.text = context.getString(R.string.hint_query_test_location, data.location)
		binding.textViewTestNo.text = context.getString(R.string.hint_query_test_test_no, data.testno)
	}
}