package com.weilylab.xhuschedule.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.TodayAdapter
import com.weilylab.xhuschedule.classes.Course
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.logs.Logs

/**
 * Created by myste.
 */
class TodayFragment : Fragment()
{
	companion object
	{
		private val TAG = "TodayFragment"

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
	private lateinit var adapter: TodayAdapter
	private var isReady = false
	private var rootView: View? = null

	override fun onCreate(savedInstanceState: Bundle?)
	{
		Logs.i(TAG, "onCreate: ")
		super.onCreate(savedInstanceState)
		@Suppress("UNCHECKED_CAST")
		list = arguments.getSerializable("list") as ArrayList<Course>
		adapter = TodayAdapter(list)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
							  savedInstanceState: Bundle?): View?
	{
		if (rootView == null)
		{
			Logs.i(TAG, "onCreateView: ")
			rootView = inflater.inflate(R.layout.fragment_today, container, false)
			val recyclerView: RecyclerView = rootView!!.findViewById(R.id.recycler_view)
			recyclerView.layoutManager = LinearLayoutManager(activity)
			recyclerView.adapter = adapter
			isReady = true
		}
		return rootView
	}

	fun refreshData()
	{
		val observer = object : Observer<Boolean>
		{
			override fun onComplete()
			{
				adapter.notifyDataSetChanged()
			}

			override fun onSubscribe(d: Disposable)
			{
			}

			override fun onError(e: Throwable)
			{
			}

			override fun onNext(t: Boolean)
			{
			}
		}
		val observable = Observable.create<Boolean> { subscriber ->
			while (true)
			{
				if (isReady)
					break
				Thread.sleep(200)
			}
			subscriber.onComplete()
		}

		observable.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer)
	}

	override fun onDestroyView()
	{
		super.onDestroyView()
		if (rootView != null)
			(rootView!!.parent as ViewGroup).removeView(rootView)
	}
}