package com.weilylab.xhuschedule.repository.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.config.APP
import com.weilylab.xhuschedule.constant.SharedPreferenceConstant
import com.weilylab.xhuschedule.repository.dataSource.InitDataSource
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import java.util.*

object InitLocalDataSource : InitDataSource {
	private val sharedPreferences = APP.context.getSharedPreferences(SharedPreferenceConstant.FILE_NAME_CONFIG, Context.MODE_PRIVATE)
	override fun getStartDateTime(startDateTimeLiveDate: MutableLiveData<PackageData<Calendar>>) {
		try {
			startDateTimeLiveDate.value = PackageData.content(getStartDataTime())
		} catch (e: Exception) {
			startDateTimeLiveDate.value = PackageData.error(e)
		}
	}

	fun setStartDataTime(startDateTime: String) {
		sharedPreferences.edit()
				.putString(SharedPreferenceConstant.FIELD_START_DATE_TIME, startDateTime)
				.apply()
	}

	fun getStartDataTime(): Calendar {
		val calendar = Calendar.getInstance()
		val dateString = sharedPreferences.getString(SharedPreferenceConstant.FIELD_START_DATE_TIME, "")
		if (dateString == "")
			return calendar
		val dateArray = dateString.split('-')
		calendar.set(dateArray[0].toInt(), dateArray[1].toInt() - 1, dateArray[2].toInt(), 0, 0, 0)
		return calendar
	}
}