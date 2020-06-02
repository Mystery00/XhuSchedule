/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.fragment

import android.graphics.Color
import android.view.ViewTreeObserver
import androidx.lifecycle.Observer
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.BaseBottomNavigationFragment
import com.weilylab.xhuschedule.config.ColorPoolHelper
import com.weilylab.xhuschedule.config.SpaceScheduleHelper
import com.weilylab.xhuschedule.databinding.FragmentTableBinding
import com.weilylab.xhuschedule.ui.custom.CustomDateAdapter
import com.weilylab.xhuschedule.ui.custom.CustomItemBuildAdapter
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.viewmodel.BottomNavigationViewModel
import com.zhuangfei.timetable.listener.ISchedule
import com.zhuangfei.timetable.listener.OnSlideBuildAdapter
import com.zhuangfei.timetable.model.Schedule
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vip.mystery0.logs.Logs
import vip.mystery0.rx.DataObserver
import vip.mystery0.tools.utils.dpTopx
import vip.mystery0.tools.utils.toDateTimeString

class TableFragment : BaseBottomNavigationFragment<FragmentTableBinding>(R.layout.fragment_table) {
	companion object {
		fun newInstance() = TableFragment()
	}

	private val bottomNavigationViewModel: BottomNavigationViewModel by sharedViewModel()

	private val courseListObserver = object : DataObserver<List<Schedule>> {
		override fun contentNoEmpty(data: List<Schedule>) {
			binding.timeTableView
					.data(data)
					.isShowNotCurWeek(ConfigurationUtil.isShowNotWeek)
					.updateView()
			bottomNavigationViewModel.week.value?.let {
				binding.timeTableView.changeWeekOnly(it)
				binding.timeTableView.onDateBuildListener()
						.onUpdateDate(binding.timeTableView.curWeek(), it)
			}
		}
	}

	override fun initView() {
		initViewModel()
		ColorPoolHelper.initColorPool(binding.timeTableView.colorPool())
		binding.timeTableView
				.curWeek(1)
				.isShowNotCurWeek(ConfigurationUtil.isShowNotWeek)
				.monthWidthDp(20)
				.alpha(0.1f, 0.06f, 0.8f)
				.callback(CustomDateAdapter())
				.callback(CustomItemBuildAdapter(requireActivity(), binding.timeTableView))
				.isShowFlaglayout(false)
//				.callback(FlagLayoutClickAdapter(binding.timeTableView))
//				.callback(SpaceItemClickAdapter(binding.timeTableView))
				.callback(OnSlideBuildAdapter()
						.setBackground(Color.BLACK)
						.setTextSize(12f)
						.setTextColor(Color.WHITE))
				.showView()
	}

	private fun initViewModel() {
		bottomNavigationViewModel.courseList.observe(requireActivity(), courseListObserver)
		bottomNavigationViewModel.week.observe(requireActivity(), Observer {
			(binding.timeTableView.onItemBuildListener() as CustomItemBuildAdapter).week = it
			binding.timeTableView.changeWeekOnly(it)
			binding.timeTableView.onDateBuildListener()
					.onUpdateDate(binding.timeTableView.curWeek(), it)
		})
		bottomNavigationViewModel.startDateTime.observe(requireActivity(), Observer {
			binding.timeTableView.curWeek(it.toDateTimeString())
		})
	}

	override fun monitor() {
		super.monitor()
		binding.timeTableView
				.callback(ISchedule.OnItemClickListener { _, scheduleList ->
					bottomNavigationViewModel.showCourse.postValue(scheduleList)
				})
		binding.timeTableView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
			override fun onGlobalLayout() {
				binding.timeTableView.viewTreeObserver.removeOnGlobalLayoutListener(this)
				val timeTableHeight = binding.timeTableView.height - dpTopx(35F)
				val itemHeight = binding.timeTableView.itemHeight()
				if (itemHeight * 11 < timeTableHeight)
					binding.timeTableView.itemHeight(timeTableHeight / 11).updateView()
			}
		})
		SpaceScheduleHelper.onSpaceScheduleClickListener = { day, start, isTwice ->
			Logs.i("monitor: $day $start $isTwice")
		}
	}

	override fun updateTitle() {
		bottomNavigationViewModel.week.value?.let {
			val whenTime = CalendarUtil.whenBeginSchool(bottomNavigationViewModel.startDateTime.value!!)
			if (it <= 0 && whenTime > 0) {
				bottomNavigationViewModel.title.postValue(Pair(javaClass, getString(R.string.hint_remain_day_of_start_term, whenTime)))
			} else {
				bottomNavigationViewModel.title.postValue(Pair(javaClass, getString(R.string.hint_week_number, it)))
			}
		}
	}
}
