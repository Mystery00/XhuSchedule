package com.weilylab.xhuschedule.ui.fragment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.FragmentTableBinding
import com.weilylab.xhuschedule.base.BaseBottomNavigationFragment
import com.weilylab.xhuschedule.config.ColorPoolHelper
import com.weilylab.xhuschedule.config.SpaceScheduleHelper
import com.weilylab.xhuschedule.config.Status.*
import com.weilylab.xhuschedule.repository.BottomNavigationRepository
import com.weilylab.xhuschedule.ui.custom.CustomDateAdapter
import com.weilylab.xhuschedule.ui.custom.CustomItemBuildAdapter
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.utils.rxAndroid.RxObserver
import com.weilylab.xhuschedule.viewModel.BottomNavigationViewModel
import com.zhuangfei.timetable.listener.ISchedule
import com.zhuangfei.timetable.listener.OnSlideBuildAdapter
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.logs.Logs
import java.text.SimpleDateFormat
import java.util.*

class TableFragment : BaseBottomNavigationFragment(R.layout.fragment_table) {
	private lateinit var fragmentTableBinding: FragmentTableBinding
	private lateinit var bottomNavigationViewModel: BottomNavigationViewModel
	private var week = 1

	private val courseListObserver = Observer<PackageData<List<Schedule>>> {
		when (it.status) {
			Content -> {
				fragmentTableBinding.timeTableView
						.data(it.data)!!
						.isShowNotCurWeek(ConfigurationUtil.isShowNotWeek)
						.updateView()
			}
		}
	}

	private val weekObserver = Observer<Int> {
		try {
			fragmentTableBinding.timeTableView.changeWeekOnly(it)
			fragmentTableBinding.timeTableView.onDateBuildListener().onUpdateDate(fragmentTableBinding.timeTableView.curWeek(), it)
		} catch (e: Exception) {
			week = it
		}
	}

	private val startDateTimeObserver = Observer<PackageData<Calendar>> {
		when (it.status) {
			Content -> {
				val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
				fragmentTableBinding.timeTableView
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
		ColorPoolHelper.initColorPool(fragmentTableBinding.timeTableView.colorPool())
		fragmentTableBinding.timeTableView
				.curWeek(week)
				.isShowNotCurWeek(ConfigurationUtil.isShowNotWeek)
				.alpha(0.1f, 0.05f, 1f)
				.callback(CustomDateAdapter())
				.callback(CustomItemBuildAdapter())
				.isShowFlaglayout(false)
//				.callback(FlagLayoutClickAdapter(fragmentTableBinding.timeTableView))
//				.callback(SpaceItemClickAdapter(fragmentTableBinding.timeTableView))
				.callback(OnSlideBuildAdapter()
						.setBackground(Color.BLACK)
						.setTextSize(12f)
						.setTextColor(Color.WHITE))
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
				.callback(ISchedule.OnItemClickListener { _, scheduleList ->
					bottomNavigationViewModel.showCourse.value = scheduleList
				})
		SpaceScheduleHelper.onSpaceScheduleClickListener = { day, start, isTwice ->
			Logs.i("monitor: $day $start $isTwice")
		}
	}

	override fun onResume() {
		super.onResume()
		if (LayoutRefreshConfigUtil.isRefreshTableFragment) {
			if (!LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity)
				BottomNavigationRepository.queryCacheCourses(bottomNavigationViewModel)
			LayoutRefreshConfigUtil.isRefreshTableFragment = false
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
						if (data != null && data) {
							if (bottomNavigationViewModel.week.value != null && bottomNavigationViewModel.week.value!!.toInt() <= 0) {
								val whenTime = CalendarUtil.whenBeginSchool()
								if (whenTime > 0)
									bottomNavigationViewModel.title.value = "距离开学还有${whenTime}天"
								else
									bottomNavigationViewModel.title.value = "第${bottomNavigationViewModel.week.value
											?: "0"}周"
							} else
								bottomNavigationViewModel.title.value = "第${bottomNavigationViewModel.week.value
										?: "0"}周"
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
					}
				})
	}
}
