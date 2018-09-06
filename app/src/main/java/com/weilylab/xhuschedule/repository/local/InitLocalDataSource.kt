package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.repository.dataSource.InitDataSource
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import vip.mystery0.rxpackagedata.PackageData
import java.util.*

object InitLocalDataSource : InitDataSource {
	override fun getStartDateTime(startDateTimeLiveDate: MutableLiveData<PackageData<Calendar>>) {
		try {
			startDateTimeLiveDate.value = PackageData.content(getStartDateTime())
		} catch (e: Exception) {
			startDateTimeLiveDate.value = PackageData.error(e)
		}
	}

	fun setStartDateTime(startDateTime: String) {
		ConfigurationUtil.startTime = startDateTime
	}

	fun getStartDateTime(): Calendar {
		val calendar = Calendar.getInstance()
		val dateString = if (ConfigurationUtil.isCustomStartTime)
			ConfigurationUtil.customStartTime
		else
			ConfigurationUtil.startTime
		if (dateString == "")
			return calendar
		val dateArray = dateString.split('-')
		calendar.set(dateArray[0].toInt(), dateArray[1].toInt() - 1, dateArray[2].toInt(), 0, 0, 0)
		return calendar
	}
}