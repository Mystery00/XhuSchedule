package com.weilylab.xhuschedule.newPackage.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.FragmentTodayBinding
import com.weilylab.xhuschedule.newPackage.model.Course
import com.weilylab.xhuschedule.newPackage.ui.adapter.FragmentTodayRecyclerViewAdapter
import com.weilylab.xhuschedule.newPackage.viewModel.BottomNavigationViewModel
import vip.mystery0.tools.base.BaseFragment
import java.util.ArrayList

class TodayFragment : BaseFragment(R.layout.fragment_today) {

	companion object {
		fun newInstance() = TodayFragment()
	}

	private lateinit var viewModel: BottomNavigationViewModel
	private lateinit var fragmentTodayBinding: FragmentTodayBinding
	private lateinit var adapter: FragmentTodayRecyclerViewAdapter
	private val list = ArrayList<Course>()

	private val todayCourseListObserver = Observer<List<Course>> {
		list.clear()
		list.addAll(it)
		adapter.notifyDataSetChanged()
	}

	override fun inflateView(layoutId: Int, inflater: LayoutInflater, container: ViewGroup?): View {
		fragmentTodayBinding = FragmentTodayBinding.inflate(LayoutInflater.from(activity), container, false)
		return fragmentTodayBinding.root
	}

	override fun initView() {
		initViewModel()
		fragmentTodayBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
		adapter = FragmentTodayRecyclerViewAdapter(activity, list)
		fragmentTodayBinding.recyclerView.adapter = adapter
	}

	private fun initViewModel() {
		viewModel = ViewModelProviders.of(activity!!).get(BottomNavigationViewModel::class.java)
		viewModel.todayCourseList.observe(activity!!, todayCourseListObserver)
	}

	override fun monitor() {
		super.monitor()
	}
}
