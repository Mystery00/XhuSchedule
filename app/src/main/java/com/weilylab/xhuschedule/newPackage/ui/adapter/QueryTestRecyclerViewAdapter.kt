package com.weilylab.xhuschedule.newPackage.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemQueryTestBinding
import com.weilylab.xhuschedule.newPackage.model.Test
import vip.mystery0.tools.base.BaseRecyclerViewAdapter

class QueryTestRecyclerViewAdapter(val context: Context, list: ArrayList<in Test>) : BaseRecyclerViewAdapter<QueryTestRecyclerViewAdapter.ViewHolder, Test>(context, R.layout.item_query_test, list) {
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(ItemQueryTestBinding.inflate(LayoutInflater.from(parent.context), parent, false))

	override fun setItemView(holder: ViewHolder, position: Int, data: Test) {
		holder.itemQueryTestBinding.test = data
		holder.itemQueryTestBinding.textViewTestDate.text = context.getString(R.string.hint_query_test_date, data.date)
		holder.itemQueryTestBinding.textViewTestTime.text = context.getString(R.string.hint_query_test_time, data.time)
		holder.itemQueryTestBinding.textViewTestLocation.text = context.getString(R.string.hint_query_test_location, data.location)
		holder.itemQueryTestBinding.textViewTestNo.text = context.getString(R.string.hint_query_test_test_no, data.testno)
	}

	inner class ViewHolder(val itemQueryTestBinding: ItemQueryTestBinding) : RecyclerView.ViewHolder(itemQueryTestBinding.root)
}