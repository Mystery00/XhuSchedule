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
import vip.mystery0.tools.ResourceException
import vip.mystery0.tools.packageName
import java.util.*
import kotlin.collections.ArrayList

private const val CALENDARS_ACCOUNT_TYPE = CalendarContract.ACCOUNT_TYPE_LOCAL

/**
 * 检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
 */
private fun checkAndAddCalendarAccount(context: Context, accountName: String): Long {
    return checkCalendarAccount(context, accountName)
            ?: return addCalendarAccount(context, accountName)
                    ?: throw ResourceException(R.string.error_calendar_account_create_failed)
}

/**
 * 检查是否存在现有账户
 */
private fun checkCalendarAccount(context: Context, accountName: String): Long? {
    val selection = "${CalendarContract.Calendars.ACCOUNT_NAME} = ? and ${CalendarContract.Calendars.ACCOUNT_TYPE} = ?"
    val selectionArgs = arrayOf(accountName, CALENDARS_ACCOUNT_TYPE)
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
 * 查询所有的日历账号
 */
fun getAllCalendarAccount(context: Context): List<Pair<String, Long>> {
    val selection = "${CalendarContract.Calendars.OWNER_ACCOUNT} = ? and ${CalendarContract.Calendars.ACCOUNT_TYPE} = ?"
    val selectionArgs = arrayOf(packageName, CALENDARS_ACCOUNT_TYPE)
    val userCursor = context.contentResolver.query(CalendarContract.Calendars.CONTENT_URI, null, selection, selectionArgs, null)
    return userCursor.use { cursor ->
        if (cursor == null) { //查询返回空值
            return emptyList()
        }
        val result = ArrayList<Pair<String, Long>>()
        while (cursor.moveToNext()) {
            result.add(Pair(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)), cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID))))
        }
        result
    }
}

/**
 * 删除日历账号
 */
fun deleteCalendarAccount(context: Context, calendarId: Long) {
    val deleteUri = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, calendarId)
    context.contentResolver.delete(deleteUri, null, null)
}

/**
 * 添加日历账户，账户创建成功则返回账户id
 */
private fun addCalendarAccount(context: Context, accountName: String): Long? {
    val values = ContentValues().apply {
        put(CalendarContract.Calendars.NAME, accountName)
        put(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
        put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
        put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, accountName)
        put(CalendarContract.Calendars.VISIBLE, 1)
        put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE)
        put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER)
        put(CalendarContract.Calendars.SYNC_EVENTS, 1)
        put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, TimeZone.getDefault().id)
        put(CalendarContract.Calendars.OWNER_ACCOUNT, packageName)
    }
    val uri = CalendarContract.Calendars.CONTENT_URI.asSyncAdapter(accountName)
    val result = context.contentResolver.insert(uri, values)
    return if (result == null) null else ContentUris.parseId(result)
}

fun deleteAllEvent(context: Context, accountName: String) {
    val calendarId = checkCalendarAccount(context, accountName) ?: return
    val deleteUri = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, calendarId)
    context.contentResolver.delete(deleteUri, null, null)
}

fun addEvent(context: Context, accountName: String, event: CalendarEvent) {
    val calendarId = checkAndAddCalendarAccount(context, accountName)
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

private fun Uri.asSyncAdapter(accountName: String): Uri {
    return buildUpon()
            .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
            .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
            .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
            .build()
}