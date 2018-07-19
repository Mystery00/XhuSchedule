package com.weilylab.xhuschedule.newPackage.ui.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.weilylab.xhuschedule.R
import vip.mystery0.logs.Logs

class TableFragment : Fragment() {
	companion object {
		fun newInstance() = TableFragment()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Logs.i("onCreate: ")
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_table, container, false)
	}
}
