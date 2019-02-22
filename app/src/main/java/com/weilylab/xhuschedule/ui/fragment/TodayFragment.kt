package com.weilylab.xhuschedule.ui.fragment

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.FragmentTodayBinding
import com.weilylab.xhuschedule.base.BaseBottomNavigationFragment
import com.weilylab.xhuschedule.databinding.LayoutNullDataViewBinding
import com.weilylab.xhuschedule.model.CustomThing
import com.weilylab.xhuschedule.repository.BottomNavigationRepository
import com.weilylab.xhuschedule.repository.JRSCRepository
import com.weilylab.xhuschedule.ui.adapter.FragmentTodayRecyclerViewAdapter
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import vip.mystery0.rxpackagedata.Status.*
import com.weilylab.xhuschedule.viewmodel.BottomNavigationViewModel
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.logs.Logs
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.rx.RxObservable
import vip.mystery0.rxpackagedata.rx.RxObserver

class TodayFragment : BaseBottomNavigationFragment<FragmentTodayBinding>(R.layout.fragment_today) {
	companion object {
		fun newInstance() = TodayFragment()
	}

	private val bottomNavigationViewModel: BottomNavigationViewModel by lazy {
		ViewModelProviders.of(activity!!)[BottomNavigationViewModel::class.java]
	}
	private var isInit = false
	private val adapter: FragmentTodayRecyclerViewAdapter by lazy { FragmentTodayRecyclerViewAdapter(activity!!) }
	private lateinit var viewStubBinding: LayoutNullDataViewBinding

	private val todayCourseListObserver = Observer<PackageData<List<Schedule>>> {
		when (it.status) {
			Content -> {
				if (it.data != null) {
					adapter.items.addAll(it.data!!)
					adapter.sortItemList {
						checkNoDataLayout()
					}
				}
			}
			Empty -> checkNoDataLayout()
			Error -> {
				Logs.wtfm("todayCourseListObserver: ", it.error)
				toastMessage(it.error?.message)
				checkNoDataLayout()
			}
		}
	}
	private val customThingListObserver = Observer<PackageData<List<CustomThing>>> {
		when (it.status) {
			Content -> {
				if (it.data != null) {
					adapter.items.addAll(it.data!!)
					adapter.sortItemList {
						checkNoDataLayout()
					}
				}
			}
			Empty -> checkNoDataLayout()
			Error -> {
				Logs.wtfm("todayCourseListObserver: ", it.error)
				checkNoDataLayout()
			}
		}
	}

	override fun initView() {
		initViewModel()
		binding.recyclerView.layoutManager = LinearLayoutManager(activity)
		binding.recyclerView.adapter = adapter
		isInit = true
		JRSCRepository.load {
			adapter.items.add(it)
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
		if (LayoutRefreshConfigUtil.isRefreshTodayFragment) {
			if (!LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity && !LayoutRefreshConfigUtil.isRefreshTableFragment)
				BottomNavigationRepository.queryCacheCourses(bottomNavigationViewModel)
			LayoutRefreshConfigUtil.isRefreshTodayFragment = false
		}
	}

	override fun updateTitle() {
		RxObservable<Boolean>()
				.doThings {
					var num = 0
					while (true) {
						when {
							isInit -> it.onFinish(true)
							num >= 10 -> it.onFinish(false)
						}
						Thread.sleep(200)
						num++
					}
				}
				.subscribe(object : RxObserver<Boolean>() {
					override fun onFinish(data: Boolean?) {
						if (data != null && data)
							if (bottomNavigationViewModel.currentWeek.value?.data != null && bottomNavigationViewModel.currentWeek.value!!.data!! <= 0) {
								val whenTime = CalendarUtil.whenBeginSchool()
								if (whenTime > 0)
									bottomNavigationViewModel.title.value = "距离开学还有${whenTime}天 ${CalendarUtil.getWeekIndexInString()}"
								else
									bottomNavigationViewModel.title.value = "第${bottomNavigationViewModel.currentWeek.value?.data
											?: "0"}周 ${CalendarUtil.getWeekIndexInString()}"
							} else
								bottomNavigationViewModel.title.value = "第${bottomNavigationViewModel.currentWeek.value?.data
										?: "0"}周 ${CalendarUtil.getWeekIndexInString()}"
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
					}
				})
	}
}
