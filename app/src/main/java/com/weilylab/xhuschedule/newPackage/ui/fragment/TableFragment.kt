package com.weilylab.xhuschedule.newPackage.ui.fragment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.FragmentTableBinding
import com.weilylab.xhuschedule.newPackage.config.Status.*
import com.weilylab.xhuschedule.newPackage.ui.custom.CustomDateAdapter
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObserver
import com.weilylab.xhuschedule.newPackage.viewModel.BottomNavigationViewModel
import com.zhuangfei.timetable.listener.OnSlideBuildAdapter
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.logs.Logs
import java.text.SimpleDateFormat
import java.util.*

class TableFragment : BaseBottomNavigationFragment(R.layout.fragment_table) {
	private lateinit var fragmentTableBinding: FragmentTableBinding
	private lateinit var bottomNavigationViewModel: BottomNavigationViewModel

	private val courseListObserver = Observer<PackageData<List<Schedule>>> {
		when (it.status) {
			Content -> fragmentTableBinding.timeTableView
					.config()
					.data(it.data)
					.toggle(fragmentTableBinding.timeTableView)
					.updateView()
		}
	}

	private val weekObserver = Observer<Int> {
		fragmentTableBinding.timeTableView.changeWeekOnly(it)
	}

	private val startDateTimeObserver = Observer<PackageData<Calendar>> {
		when (it.status) {
			Content -> {
				val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
				fragmentTableBinding.timeTableView
						.config()
						.curWeek(simpleDateFormat.format(it.data!!.time))
			}
		}
	}

	companion object {
		fun newInstance() = TableFragment()
	}

	override fun inflateView(layoutId: Int, inflater: LayoutInflater, container: ViewGroup?): View {
		fragmentTableBinding = FragmentTableBinding.inflate(inflater, container, false)
		return fragmentTableBinding.root
	}

	override fun initView() {
		initViewModel()
		fragmentTableBinding.timeTableView
				.config()
				.alpha(0.1f, 0.1f, 1f)
				.callback(CustomDateAdapter())
				.callback(OnSlideBuildAdapter()
						.setBackground(Color.BLACK)
						.setTextSize(12f)
						.setTextColor(Color.WHITE))
				.toggle(fragmentTableBinding.timeTableView)
				.showView()
	}

	private fun initViewModel() {
		bottomNavigationViewModel = ViewModelProviders.of(activity!!).get(BottomNavigationViewModel::class.java)
		bottomNavigationViewModel.courseList.observe(activity!!, courseListObserver)
		bottomNavigationViewModel.week.observe(activity!!, weekObserver)
		bottomNavigationViewModel.startDateTime.observe(activity!!, startDateTimeObserver)
	}

	override fun monitor() {
		super.monitor()
		fragmentTableBinding.timeTableView
				.config()
				.callback { _, scheduleList ->
					//					bottomNavigationViewModel.showCourse.value = scheduleList
				}
	}

	override fun updateTitle() {
		RxObservable<Boolean>()
				.doThings {
					var num = 0
					while (true) {
						when {
							::bottomNavigationViewModel.isInitialized -> it.onFinish(true)
							num >= 10 -> it.onFinish(false)
						}
						Thread.sleep(200)
						num++
					}
				}
				.subscribe(object : RxObserver<Boolean>() {
					override fun onFinish(data: Boolean?) {
						if (data != null && data)
							bottomNavigationViewModel.title.value = "第${bottomNavigationViewModel.week.value}周"
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
					}
				})
	}
}
