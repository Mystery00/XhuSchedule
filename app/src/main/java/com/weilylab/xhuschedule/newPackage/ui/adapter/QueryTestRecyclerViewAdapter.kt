package com.weilylab.xhuschedule.newPackage.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemQueryTestBinding
import com.weilylab.xhuschedule.newPackage.model.Test
import vip.mystery0.tools.base.BaseRecyclerViewAdapter

class QueryTestRecyclerViewAdapter(context: Context?, list: ArrayList<in Test>) : BaseRecyclerViewAdapter<QueryTestRecyclerViewAdapter.ViewHolder, Test>(context, R.layout.item_query_test, list) {
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(ItemQueryTestBinding.inflate(LayoutInflater.from(parent.context), parent, false))

	override fun setItemView(holder: ViewHolder, position: Int, data: Test) {
		holder.itemQueryTestBinding.test = data
	}

	inner class ViewHolder(val itemQueryTestBinding: ItemQueryTestBinding) : RecyclerView.ViewHolder(itemQueryTestBinding.root)
}