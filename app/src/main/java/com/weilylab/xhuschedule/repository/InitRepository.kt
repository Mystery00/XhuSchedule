package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.api.XhuScheduleCloudAPI
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class InitRepository : KoinComponent {
	private val xhuScheduleCloudAPI: XhuScheduleCloudAPI by inject()

	suspend fun getStartTime(): Calendar {
		return if (ConfigurationUtil.isCustomStartTime) {
			getStartDateTime()
		} else {
			val response = xhuScheduleCloudAPI.requestStartDateTime()
			if (response.isSuccessful) {
				setStartDateTime(response.data)
				val calendar = Calendar.getInstance()
				val dateArray = response.data.split('-')
				calendar.set(dateArray[0].toInt(), dateArray[1].toInt() - 1, dateArray[2].toInt(), 0, 0, 0)
				calendar
			} else {
				getStartDateTime()
			}
		}
	}

	private suspend fun setStartDateTime(startDateTime: String) {
		withContext(Dispatchers.IO) {
			ConfigurationUtil.startTime = startDateTime
		}
	}

	suspend fun getStartDateTime(): Calendar {
		val calendar = Calendar.getInstance()
		val dateString = withContext(Dispatchers.IO) {
			if (ConfigurationUtil.isCustomStartTime)
				ConfigurationUtil.customStartTime
			else
				ConfigurationUtil.startTime
		}
		if (dateString == "")
			return calendar
		return withContext(Dispatchers.Default) {
			val dateArray = dateString.split('-')
			calendar.set(dateArray[0].toInt(), dateArray[1].toInt() - 1, dateArray[2].toInt(), 0, 0, 0)
			calendar
		}
	}
}