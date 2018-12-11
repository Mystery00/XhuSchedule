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
import com.weilylab.xhuschedule.repository.BottomNavigationRepository
import com.weilylab.xhuschedule.ui.adapter.FragmentTodayRecyclerViewAdapter
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import vip.mystery0.rxpackagedata.Status.*
import com.weilylab.xhuschedule.viewModel.BottomNavigationViewModel
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.logs.Logs
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.rx.RxObservable
import vip.mystery0.rxpackagedata.rx.RxObserver

class TodayFragment : BaseBottomNavigationFragment<FragmentTodayBinding>(R.layout.fragment_today) {
	companion object {
		fun newInstance() = TodayFragment()
	}

	private lateinit var viewModel: BottomNavigationViewModel
	private lateinit var adapter: FragmentTodayRecyclerViewAdapter
	private lateinit var viewStubBinding: LayoutNullDataViewBinding

	private val todayCourseListObserver = Observer<PackageData<List<Schedule>>> {
		when (it.status) {
			Content -> {
				if (it.data != null) {
					adapter.items.clear()
					adapter.items.addAll(it.data!!)
					adapter.notifyDataSetChanged()
					hideNoDataLayout()
				}
			}
			Empty -> showNoDataLayout()
			Error -> {
				Logs.wtfm("todayCourseListObserver: ", it.error)
				toastMessage(it.error?.message)
				showNoDataLayout()
			}
		}
	}

	override fun initView() {
		initViewModel()
		binding.recyclerView.layoutManager = LinearLayoutManager(activity)
		adapter = FragmentTodayRecyclerViewAdapter(activity!!)
		binding.recyclerView.adapter = adapter
	}

	private fun initViewModel() {
		viewModel = ViewModelProviders.of(activity!!).get(BottomNavigationViewModel::class.java)
		viewModel.todayCourseList.observe(activity!!, todayCourseListObserver)
	}

	override fun monitor() {
		super.monitor()
		binding.nullDataViewStub.setOnInflateListener { _, inflated -> viewStubBinding = DataBindingUtil.bind(inflated)!! }
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
				BottomNavigationRepository.queryCacheCourses(viewModel)
			LayoutRefreshConfigUtil.isRefreshTodayFragment = false
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
							if (viewModel.currentWeek.value?.data != null && viewModel.currentWeek.value!!.data!! <= 0) {
								val whenTime = CalendarUtil.whenBeginSchool()
								if (whenTime > 0)
									viewModel.title.value = "距离开学还有${whenTime}天 ${CalendarUtil.getWeekIndexInString()}"
								else
									viewModel.title.value = "第${viewModel.currentWeek.value?.data
											?: "0"}周 ${CalendarUtil.getWeekIndexInString()}"
							} else
								viewModel.title.value = "第${viewModel.currentWeek.value?.data
										?: "0"}周 ${CalendarUtil.getWeekIndexInString()}"
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
					}
				})
	}
}
