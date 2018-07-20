package com.weilylab.xhuschedule.newPackage.repository.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.config.APP
import com.weilylab.xhuschedule.newPackage.constant.SharedPreferenceConstant
import com.weilylab.xhuschedule.newPackage.repository.dataSource.InitDataSource
import com.weilylab.xhuschedule.newPackage.utils.CalendarUtil
import java.util.*

object InitLocalDataSource : InitDataSource {
	private val sharedPreferences = APP.context.getSharedPreferences(SharedPreferenceConstant.FILE_NAME_CONFIG, Context.MODE_PRIVATE)
	override fun getStartDateTime(startDateTimeLiveDate: MutableLiveData<Calendar>, weekLiveData: MutableLiveData<Int>) {
		val startDateTime = getStartDataTime()
		startDateTimeLiveDate.value = startDateTime
		weekLiveData.value = CalendarUtil.getWeekFromCalendar(startDateTime)
	}

	fun setStartDataTime(startDateTime: String) {
		sharedPreferences.edit()
				.putString(SharedPreferenceConstant.FIELD_START_DATE_TIME, startDateTime)
				.apply()
	}

	private fun getStartDataTime(): Calendar {
		val calendar = Calendar.getInstance()
		val dateString = sharedPreferences.getString(SharedPreferenceConstant.FIELD_START_DATE_TIME, "")
		if (dateString == "")
			return calendar
		val dateArray = dateString.split('-')
		calendar.set(dateArray[0].toInt(), dateArray[1].toInt() - 1, dateArray[2].toInt(), 0, 0, 0)
		return calendar
	}
}