package com.weilylab.xhuschedule.ui.activity

import com.applikeysolutions.cosmocalendar.model.Day
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.repository.local.InitLocalDataSource
import com.weilylab.xhuschedule.utils.CalendarUtil
import kotlinx.android.synthetic.main.activity_custom_start_time.*
import java.util.*

class CustomStartTimeActivity : XhuBaseActivity(R.layout.activity_custom_start_time) {
	override fun initData() {
		super.initData()
		calendarView.selectionManager.toggleDay(Day(InitLocalDataSource.getStartDateTime()))
	}

	override fun monitor() {
		super.monitor()
		buttonOk.setOnClickListener {
			finishAndSetStartTime(calendarView.selectedDates[0])
		}
		buttonCancel.setOnClickListener {
			finish()
		}
		buttonDefault.setOnClickListener {
			finishAndSetStartTime(null)
		}
	}

	private fun finishAndSetStartTime(startDateTime: Calendar?) {
		CalendarUtil.setCustomStartTime(getMondayForCalendar(startDateTime))
		LayoutRefreshConfigUtil.isRefreshStartTime = true
		LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity = true
		finish()
	}

	private fun getMondayForCalendar(calendar: Calendar?): Calendar? {
		if (calendar == null)
			return null
		return if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
			calendar
		else {
			while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
				calendar.add(Calendar.DATE, -1)
			calendar
		}
	}
}
