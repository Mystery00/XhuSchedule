package com.weilylab.xhuschedule.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.TodayAdapter
import com.weilylab.xhuschedule.classes.Course

/**
 * Created by myste.
 */
class TodayFragment : Fragment()
{
	companion object
	{
		fun newInstance(list: ArrayList<Course>): TodayFragment
		{
			val bundle = Bundle()
			bundle.putSerializable("list", list)
			val fragment = TodayFragment()
			fragment.arguments = bundle
			return fragment
		}
	}

	private lateinit var list: ArrayList<Course>
	lateinit var adapter: TodayAdapter

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		@Suppress("UNCHECKED_CAST")
		list = arguments.getSerializable("list") as ArrayList<Course>
		adapter = TodayAdapter(list)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
							  savedInstanceState: Bundle?): View?
	{
		val view = inflater.inflate(R.layout.fragment_today, container, false)
		val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
		recyclerView.layoutManager = LinearLayoutManager(activity)
		recyclerView.adapter = adapter
		return view
	}
}