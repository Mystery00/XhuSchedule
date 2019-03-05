package com.weilylab.xhuschedule.ui.fragment

import android.graphics.Color
import android.view.ViewTreeObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.FragmentTableBinding
import com.weilylab.xhuschedule.base.BaseBottomNavigationFragment
import com.weilylab.xhuschedule.config.ColorPoolHelper
import com.weilylab.xhuschedule.config.SpaceScheduleHelper
import vip.mystery0.rx.Status.*
import com.weilylab.xhuschedule.repository.BottomNavigationRepository
import com.weilylab.xhuschedule.ui.custom.CustomDateAdapter
import com.weilylab.xhuschedule.ui.custom.CustomItemBuildAdapter
import com.weilylab.xhuschedule.utils.*
import com.weilylab.xhuschedule.viewmodel.BottomNavigationViewModel
import com.zhuangfei.timetable.listener.ISchedule
import com.zhuangfei.timetable.listener.OnSlideBuildAdapter
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.logs.Logs
import vip.mystery0.rx.PackageData
import vip.mystery0.tools.utils.DensityTools
import java.text.SimpleDateFormat
import java.util.*

class TableFragment : BaseBottomNavigationFragment<FragmentTableBinding>(R.layout.fragment_table) {
	companion object {
		fun newInstance() = TableFragment()
	}

	private val bottomNavigationViewModel: BottomNavigationViewModel by lazy {
		ViewModelProviders.of(activity!!)[BottomNavigationViewModel::class.java]
	}
	private var week = 1

	private val courseListObserver = Observer<PackageData<List<Schedule>>> {
		when (it.status) {
			Content -> {
				binding.timeTableView
						.data(it.data)!!
						.isShowNotCurWeek(ConfigurationUtil.isShowNotWeek)
						.updateView()
			}
			else -> {
			}
		}
	}

	private val itemHeightObserver = Observer<Int> {
		binding.timeTableView.itemHeight(it).updateView()
	}

	private val weekObserver = Observer<Int> {
		try {
			(binding.timeTableView.onItemBuildListener() as CustomItemBuildAdapter).week = it
			binding.timeTableView.changeWeekOnly(it)
			binding.timeTableView.onDateBuildListener()
					.onUpdateDate(binding.timeTableView.curWeek(), it)
		} catch (e: Exception) {
			week = it
		}
	}

	private val startDateTimeObserver = Observer<PackageData<Calendar>> {
		when (it.status) {
			Content -> {
				val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
				binding.timeTableView.curWeek(simpleDateFormat.format(it.data!!.time))
			}
			else -> {
			}
		}
	}

	override fun initView() {
		initViewModel()
		ColorPoolHelper.initColorPool(binding.timeTableView.colorPool())
		binding.timeTableView
				.curWeek(week)
				.isShowNotCurWeek(ConfigurationUtil.isShowNotWeek)
				.monthWidthDp(20)
				.alpha(0.1f, 0.06f, 0.8f)
				.callback(CustomDateAdapter())
				.callback(CustomItemBuildAdapter(activity!!, binding.timeTableView))
				.isShowFlaglayout(false)
//				.callback(FlagLayoutClickAdapter(binding.timeTableView))
//				.callback(SpaceItemClickAdapter(binding.timeTableView))
				.callback(OnSlideBuildAdapter()
						.setBackground(Color.BLACK)
						.setTextSize(12f)
						.setTextColor(Color.WHITE))
				.showView()
		updateTitle()
	}

	private fun initViewModel() {
		bottomNavigationViewModel.courseList.observe(activity!!, courseListObserver)
		bottomNavigationViewModel.week.observe(activity!!, weekObserver)
		bottomNavigationViewModel.startDateTime.observe(activity!!, startDateTimeObserver)
		bottomNavigationViewModel.itemHeight.observe(activity!!, itemHeightObserver)
	}

	override fun monitor() {
		super.monitor()
		binding.timeTableView
				.callback(ISchedule.OnItemClickListener { _, scheduleList ->
					bottomNavigationViewModel.showCourse.value = scheduleList
				})
		binding.timeTableView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
			override fun onGlobalLayout() {
				binding.timeTableView.viewTreeObserver.removeOnGlobalLayoutListener(this)
				val timeTableHeight = binding.timeTableView.height - DensityTools.dp2px(35F)
				val itemHeight = binding.timeTableView.itemHeight()
				if (itemHeight * 11 < timeTableHeight)
					bottomNavigationViewModel.itemHeight.value = timeTableHeight / 11
			}
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
		if (activity == null)
			return
		if (bottomNavigationViewModel.week.value == null)
			return
		if (bottomNavigationViewModel.week.value!!.toInt() <= 0) {
			val whenTime = CalendarUtil.whenBeginSchool()
			if (whenTime > 0)
				bottomNavigationViewModel.title.value = getString(R.string.hint_remain_day_of_start_term, whenTime)
			else
				bottomNavigationViewModel.title.value = getString(R.string.hint_week_number_s, bottomNavigationViewModel.week.value
						?: "0")
		} else
			bottomNavigationViewModel.title.value = getString(R.string.hint_week_number_s, bottomNavigationViewModel.week.value
					?: "0")
	}
}
