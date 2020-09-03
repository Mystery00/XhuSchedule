/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.provider.CalendarContract
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.model.CalendarAttendee
import com.weilylab.xhuschedule.model.CalendarEvent
import com.weilylab.xhuschedule.model.Student
import vip.mystery0.tools.ResourceException
import vip.mystery0.tools.packageName
import java.util.*

private val CALENDARS_ACCOUNT_TYPE = packageName

/**
 * 检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
 */
private fun checkAndAddCalendarAccount(context: Context, student: Student): Long {
	return checkCalendarAccount(context, student) ?: return addCalendarAccount(context, student)
			?: throw ResourceException(R.string.error_calendar_account_create_failed)
}

/**
 * 检查是否存在现有账户
 */
private fun checkCalendarAccount(context: Context, student: Student): Long? {
	val selection = "${CalendarContract.Calendars.ACCOUNT_NAME} = ? and ${CalendarContract.Calendars.ACCOUNT_TYPE} = ?"
	val selectionArgs = arrayOf(student.calendarAccountName, CALENDARS_ACCOUNT_TYPE)
	val userCursor = context.contentResolver.query(CalendarContract.Calendars.CONTENT_URI, null, selection, selectionArgs, null)
	return userCursor.use { cursor ->
		if (cursor == null) { //查询返回空值
			return null
		}
		val count: Int = cursor.count
		if (count > 0) { //存在现有账户，取第一个账户的id返回
			cursor.moveToFirst()
			cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID))
		} else {
			null
		}
	}
}

/**
 * 添加日历账户，账户创建成功则返回账户id
 */
private fun addCalendarAccount(context: Context, student: Student): Long? {
	val name = student.calendarAccountName
	val values = ContentValues().apply {
		put(CalendarContract.Calendars.NAME, name)
		put(CalendarContract.Calendars.ACCOUNT_NAME, name)
		put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
		put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, name)
		put(CalendarContract.Calendars.VISIBLE, 1)
		put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE)
		put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER)
		put(CalendarContract.Calendars.SYNC_EVENTS, 1)
		put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, TimeZone.getDefault().id)
		put(CalendarContract.Calendars.OWNER_ACCOUNT, name)
	}
	val uri = CalendarContract.Calendars.CONTENT_URI.asSyncAdapter(student)
	val result = context.contentResolver.insert(uri, values)
	return if (result == null) null else ContentUris.parseId(result)
}

fun deleteAllEvent(context: Context, student: Student) {
	val calendarId = checkCalendarAccount(context, student) ?: return
	val deleteUri = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, calendarId)
	context.contentResolver.delete(deleteUri, null, null)
}

fun addEvent(context: Context, student: Student, event: CalendarEvent) {
	val calendarId = checkAndAddCalendarAccount(context, student)
	val values = ContentValues().apply {
		put(CalendarContract.Events.CALENDAR_ID, calendarId)
		put(CalendarContract.Events.TITLE, event.title)
		put(CalendarContract.Events.DTSTART, event.startTime)
		put(CalendarContract.Events.DTEND, event.endTime)
		put(CalendarContract.Events.EVENT_LOCATION, event.location)
		put(CalendarContract.Events.DESCRIPTION, event.description)
		put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
		put(CalendarContract.Events.ALL_DAY, if (event.allDay) 1 else 0)
	}
	val result = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
			?: throw Exception(context.getString(R.string.error_calendar_insert_failed, event.title))
	val eventId = ContentUris.parseId(result)
	event.attendees.forEach { addAttendees(context, eventId, it) }
	event.reminder.forEach { addReminder(context, eventId, it) }
}

private fun addAttendees(context: Context, eventId: Long, attendee: CalendarAttendee) {
	val values = ContentValues().apply {
		put(CalendarContract.Attendees.EVENT_ID, eventId)
		put(CalendarContract.Attendees.ATTENDEE_NAME, attendee.name)
	}
	context.contentResolver.insert(CalendarContract.Attendees.CONTENT_URI, values)
}

private fun addReminder(context: Context, eventId: Long, minutes: Int) {
	val values = ContentValues().apply {
		put(CalendarContract.Reminders.EVENT_ID, eventId)
		put(CalendarContract.Reminders.MINUTES, minutes)
		put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
	}
	context.contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, values)
}

private val Student.calendarAccountName
	get() = "${username}@西瓜课表"

private fun Uri.asSyncAdapter(student: Student): Uri {
	return buildUpon()
			.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
			.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, student.calendarAccountName)
			.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
			.build()
}