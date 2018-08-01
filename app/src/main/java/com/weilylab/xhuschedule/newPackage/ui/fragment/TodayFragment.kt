package com.weilylab.xhuschedule.newPackage.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.FragmentTodayBinding
import com.weilylab.xhuschedule.newPackage.ui.adapter.FragmentTodayRecyclerViewAdapter
import com.weilylab.xhuschedule.newPackage.utils.CalendarUtil
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObserver
import com.weilylab.xhuschedule.newPackage.viewModel.BottomNavigationViewModel
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.logs.Logs
import java.util.ArrayList

class TodayFragment : BaseBottomNavigationFragment(R.layout.fragment_today) {

	companion object {
		fun newInstance() = TodayFragment()
	}

	private lateinit var viewModel: BottomNavigationViewModel
	private lateinit var fragmentTodayBinding: FragmentTodayBinding
	private lateinit var adapter: FragmentTodayRecyclerViewAdapter
	private val list = ArrayList<Schedule>()

	private val todayCourseListObserver = Observer<List<Schedule>> {
		list.clear()
		list.addAll(it)
		adapter.notifyDataSetChanged()
		if (list.size == 0)
			fragmentTodayBinding.nullDataView.visibility = View.VISIBLE
		else
			fragmentTodayBinding.nullDataView.visibility = View.GONE
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
		adapter.setOnItemClickListener { _, course ->
			viewModel.showCourse.value = arrayListOf(course)
			true
		}
	}

	override fun updateTitle() {
		RxObservable<Boolean>()
				.doThings {
					var num = 0
					while (true) {
						when {
							::viewModel.isInitialized -> it.onFinish(true)
							num >= 10 -> it.onFinish(false)
						}
						Thread.sleep(200)
						num++
					}
				}
				.subscribe(object : RxObserver<Boolean>() {
					override fun onFinish(data: Boolean?) {
						if (data != null && data)
							viewModel.title.value = "第${viewModel.currentWeek.value}周 ${CalendarUtil.getWeekIndexInString()}"
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
					}
				})
	}
}
