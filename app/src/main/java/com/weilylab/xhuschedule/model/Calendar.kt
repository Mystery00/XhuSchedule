/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.model

data class CalendarAttendee(var name: String)

data class CalendarEvent(var title: String,
						 var startTime: Long,
						 var endTime: Long,
						 var location: String,
						 var description: String,
						 var allDay: Boolean,
						 var hasAlarm: Boolean) {
	var attendees = ArrayList<CalendarAttendee>()
	var reminder = ArrayList<Int>()
}