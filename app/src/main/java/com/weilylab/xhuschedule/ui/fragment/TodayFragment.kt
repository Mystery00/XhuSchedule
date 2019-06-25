package com.weilylab.xhuschedule.ui.fragment

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.BaseBottomNavigationFragment
import com.weilylab.xhuschedule.config.APP
import com.weilylab.xhuschedule.databinding.FragmentTodayBinding
import com.weilylab.xhuschedule.databinding.LayoutNullDataViewBinding
import com.weilylab.xhuschedule.model.CustomThing
import com.weilylab.xhuschedule.repository.BottomNavigationRepository
import com.weilylab.xhuschedule.repository.JRSCRepository
import com.weilylab.xhuschedule.ui.adapter.FragmentTodayRecyclerViewAdapter
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import com.weilylab.xhuschedule.viewmodel.BottomNavigationViewModel
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.logs.Logs
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.Status.*

class TodayFragment : BaseBottomNavigationFragment<FragmentTodayBinding>(R.layout.fragment_today) {
	companion object {
		fun newInstance() = TodayFragment()
	}

	private val bottomNavigationViewModel: BottomNavigationViewModel by lazy {
		ViewModelProviders.of(activity!!)[BottomNavigationViewModel::class.java]
	}
	private val adapter: FragmentTodayRecyclerViewAdapter by lazy { FragmentTodayRecyclerViewAdapter(activity!!) }
	private lateinit var viewStubBinding: LayoutNullDataViewBinding

	private val todayCourseListObserver = Observer<PackageData<List<Schedule>>> { data ->
		when (data.status) {
			Content -> {
				if (data.data != null) {
					adapter.tempList.removeAll(adapter.items.filterIsInstance<Schedule>())
					adapter.tempList.addAll(data.data!!)
					adapter.sortItemList {
						checkNoDataLayout()
					}
				}
			}
			Empty -> {
				adapter.tempList.removeAll(adapter.items.filterIsInstance<Schedule>())
				adapter.sortItemList {
					checkNoDataLayout()
				}
			}
			Error -> {
				Logs.wtfm("todayCourseListObserver: ", data.error)
				toastMessage(data.error?.message)
				checkNoDataLayout()
			}
			Loading -> {
			}
		}
	}
	private val customThingListObserver = Observer<PackageData<List<CustomThing>>> { data ->
		when (data.status) {
			Content -> {
				if (data.data != null) {
					adapter.tempList.removeAll(adapter.items.filterIsInstance<CustomThing>())
					adapter.tempList.addAll(data.data!!)
					adapter.sortItemList {
						checkNoDataLayout()
					}
				}
			}
			Empty -> {
				adapter.tempList.removeAll(adapter.items.filterIsInstance<CustomThing>())
				adapter.sortItemList {
					checkNoDataLayout()
				}
			}
			Error -> {
				Logs.wtfm("todayCourseListObserver: ", data.error)
				checkNoDataLayout()
			}
			Loading -> {
			}
		}
	}

	override fun initView() {
		initViewModel()
		binding.recyclerView.layoutManager = LinearLayoutManager(activity)
		binding.recyclerView.adapter = adapter
		updateTitle()
		JRSCRepository.load(APP.context) {
			adapter.tempList.add(it)
			adapter.sortItemList {
				checkNoDataLayout()
			}
		}
	}

	private fun initViewModel() {
		bottomNavigationViewModel.todayCourseList.observe(activity!!, todayCourseListObserver)
		bottomNavigationViewModel.customThingList.observe(activity!!, customThingListObserver)
	}

	override fun monitor() {
		super.monitor()
		binding.nullDataViewStub.setOnInflateListener { _, inflated -> viewStubBinding = DataBindingUtil.bind(inflated)!! }
	}

	private fun checkNoDataLayout() {
		if (adapter.items.isNotEmpty())
			hideNoDataLayout()
		else
			showNoDataLayout()
	}

	private fun showNoDataLayout() {
		if (!binding.nullDataViewStub.isInflated)
			binding.nullDataViewStub.viewStub!!.inflate()
		else
			viewStubBinding.root.visibility = View.VISIBLE
		binding.line.visibility = View.GONE
		binding.recyclerView.visibility = View.GONE
	}

	private fun hideNoDataLayout() {
		if (binding.nullDataViewStub.isInflated)
			viewStubBinding.root.visibility = View.GONE
		binding.line.visibility = View.VISIBLE
		binding.recyclerView.visibility = View.VISIBLE
	}

	override fun onResume() {
		super.onResume()
		Logs.i("onResume: ${LayoutRefreshConfigUtil.isChangeShowTomorrowAfterOnTodayFragment}")
		if ((LayoutRefreshConfigUtil.isRefreshTodayFragment && !LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity && !LayoutRefreshConfigUtil.isRefreshTableFragment) || LayoutRefreshConfigUtil.isChangeShowTomorrowAfterOnTodayFragment) {
			BottomNavigationRepository.queryCacheCourses(bottomNavigationViewModel)
		}
		if (LayoutRefreshConfigUtil.isChangeShowTomorrowAfterOnTodayFragment)
			updateTitle()
		LayoutRefreshConfigUtil.isRefreshTodayFragment = false
		LayoutRefreshConfigUtil.isChangeShowTomorrowAfterOnTodayFragment = false
	}

	override fun updateTitle() {
		if (activity == null)
			return
		if (bottomNavigationViewModel.currentWeek.value?.data == null)
			return
		val shouldShowTomorrow = CalendarUtil.shouldShowTomorrowInfo()
		val whenTime = CalendarUtil.whenBeginSchool(shouldShowTomorrow)
		val weekIndex = CalendarUtil.getWeekIndexInString(if (shouldShowTomorrow) CalendarUtil.getTomorrowIndex() else CalendarUtil.getWeekIndex())
		if (bottomNavigationViewModel.currentWeek.value!!.data!! <= 0 && whenTime > 0) {
			bottomNavigationViewModel.title.value = "距离开学还有${whenTime}天 $weekIndex"
		} else {
			var week = bottomNavigationViewModel.currentWeek.value?.data
			if (week != null && shouldShowTomorrow) {
				val index = CalendarUtil.getWeekIndex()
				if (index == 7)//如果今天是周日，那么明天就是周一,周数加一
					week++
			}
			bottomNavigationViewModel.title.value = "第${week ?: "0"}周 $weekIndex"
		}
	}
}
