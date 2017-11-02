package com.weilylab.xhuschedule.util

/**
 * Created by myste.
 */
class ScheduleHelper private constructor()
{
	companion object
	{
		private var scheduleHelper: ScheduleHelper? = null

		fun getInstance(): ScheduleHelper
		{
			if (scheduleHelper == null)
				scheduleHelper = ScheduleHelper()
			return scheduleHelper!!
		}
	}

	var isCookieAvailable = false
}