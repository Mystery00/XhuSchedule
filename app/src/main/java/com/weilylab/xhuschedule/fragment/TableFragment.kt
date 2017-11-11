package com.weilylab.xhuschedule.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.TableAdapter
import com.weilylab.xhuschedule.classes.Course
import vip.mystery0.tools.logs.Logs

/**
 * Created by myste.
 */
class TableFragment : Fragment()
{
	companion object
	{
		private val TAG = "TableFragment"

		fun newInstance(list: ArrayList<Course?>): TableFragment
		{
			val bundle = Bundle()
			bundle.putSerializable("list", list)
			val fragment = TableFragment()
			fragment.arguments = bundle
			return fragment
		}
	}

	private lateinit var list: ArrayList<Course?>
	lateinit var adapter: TableAdapter

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		@Suppress("UNCHECKED_CAST")
		list = arguments.getSerializable("list") as ArrayList<Course?>
		adapter = TableAdapter(activity, list)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
							  savedInstanceState: Bundle?): View?
	{
		val view = inflater.inflate(R.layout.fragment_table, container, false)
		val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
		recyclerView.layoutManager = GridLayoutManager(activity, 7, StaggeredGridLayoutManager.VERTICAL, false)
		recyclerView.adapter = adapter
		return view
	}
}