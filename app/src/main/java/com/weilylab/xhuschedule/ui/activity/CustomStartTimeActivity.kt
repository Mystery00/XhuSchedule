/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.activity

import com.applikeysolutions.cosmocalendar.model.Day
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.model.event.UI
import com.weilylab.xhuschedule.model.event.UIConfigEvent
import com.weilylab.xhuschedule.repository.InitRepository
import com.weilylab.xhuschedule.utils.CalendarUtil
import kotlinx.android.synthetic.main.activity_custom_start_time.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import java.util.*

class CustomStartTimeActivity : XhuBaseActivity(R.layout.activity_custom_start_time) {
	private val eventBus: EventBus by inject()

	private val initRepository: InitRepository by inject()

	override fun initData() {
		super.initData()
		launch {
			calendarView.selectionManager.toggleDay(Day(initRepository.getStartTime()))
		}
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
		eventBus.post(UIConfigEvent(arrayListOf(UI.MAIN_INIT)))
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
