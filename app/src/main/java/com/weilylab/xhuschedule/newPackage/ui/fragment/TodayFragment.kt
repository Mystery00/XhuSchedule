package com.weilylab.xhuschedule.newPackage.ui.fragment

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.newPackage.viewModel.BottomNavigationViewModel
import vip.mystery0.logs.Logs
import vip.mystery0.tools.base.BaseFragment

class TodayFragment : BaseFragment(R.layout.fragment_today) {

	companion object {
		fun newInstance() = TodayFragment()
	}

	private lateinit var viewModel: BottomNavigationViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Logs.i("onCreate: ")
	}

	override fun initView() {
		viewModel = ViewModelProviders.of(activity!!).get(BottomNavigationViewModel::class.java)
	}

	override fun monitor() {
		super.monitor()
	}
}
