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
import com.weilylab.xhuschedule.module.check
import com.weilylab.xhuschedule.module.checkConnect
import org.koin.core.KoinComponent
import org.koin.core.inject

class SchoolCalendarRepository : KoinComponent {
    private val xhuScheduleCloudAPI: XhuScheduleCloudAPI by inject()

    suspend fun getUrl(listener: (String?) -> Unit) = checkConnect {
        val response = xhuScheduleCloudAPI.schoolCalendar().check()
        listener(response.data)
    }
}