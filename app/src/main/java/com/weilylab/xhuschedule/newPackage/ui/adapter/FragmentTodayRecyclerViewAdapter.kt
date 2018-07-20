package com.weilylab.xhuschedule.newPackage.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemFragmentTodayBinding
import com.weilylab.xhuschedule.newPackage.model.Course
import vip.mystery0.tools.base.BaseRecyclerViewAdapter

class FragmentTodayRecyclerViewAdapter(context: Context?, list: ArrayList<in Course>) : BaseRecyclerViewAdapter<FragmentTodayRecyclerViewAdapter.ViewHolder, Course>(context, R.layout.item_fragment_today, list) {
	private lateinit var itemFragmentTodayBinding: ItemFragmentTodayBinding

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		itemFragmentTodayBinding = ItemFragmentTodayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ViewHolder(itemFragmentTodayBinding)
	}

	override fun setItemView(holder: ViewHolder, position: Int, data: Course) {
		itemFragmentTodayBinding.course = data
	}

	inner class ViewHolder(itemFragmentTodayBinding: ItemFragmentTodayBinding) : RecyclerView.ViewHolder(itemFragmentTodayBinding.root)
}