/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.api.XhuScheduleCloudAPI
import com.weilylab.xhuschedule.utils.CalendarUtil.parseCalendar
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
                response.data.parseCalendar()
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
            dateString.parseCalendar()
        }
    }
}