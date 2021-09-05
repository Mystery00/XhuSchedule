/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.model.*
import com.weilylab.xhuschedule.repository.*
import com.weilylab.xhuschedule.utils.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.content
import vip.mystery0.rx.empty
import vip.mystery0.rx.launch
import vip.mystery0.tools.context

class SettingsViewModel : ViewModel(), KoinComponent {
    private val studentRepository: StudentRepository by inject()
    private val initRepository: InitRepository by inject()
    private val schoolCalendarRepository: SchoolCalendarRepository by inject()
    private val courseRepository: CourseRepository by inject()
    private val customThingRepository: CustomThingRepository by inject()

    //学生列表
    val studentList by lazy { MutableLiveData<PackageData<List<Student>>>() }

    //所有学生信息列表
    val studentInfoList by lazy { MutableLiveData<PackageData<List<StudentInfo>>>() }

    val exportCalendar by lazy { MutableLiveData<PackageData<Boolean>>() }

    fun initStudentList() {
        launch(studentList) {
            initStudentListInCoroutine()
        }
    }

    private suspend fun initStudentListInCoroutine() {
        val list = studentRepository.queryAllStudentList()
        if (list.isNullOrEmpty()) {
            studentList.empty()
        } else {
            studentList.content(list)
        }
    }

    fun updateStudentList(updateList: List<Student>) {
        launch(studentList) {
            studentRepository.updateStudentList(updateList)
            initStudentListInCoroutine()
        }
    }

    fun deleteStudentList(deleteList: List<Student>) {
        launch(studentList) {
            studentRepository.deleteStudentList(deleteList)
            initStudentListInCoroutine()
        }
    }

    fun queryAllStudentListAndThen(block: (List<Student>) -> Unit) {
        launch(studentList) {
            val list = if (studentList.value == null) {
                val list = studentRepository.queryAllStudentList()
                if (list.isNullOrEmpty()) {
                    studentList.empty()
                } else {
                    studentList.content(list)
                }
                list
            } else {
                studentList.value?.data
            }
            list?.let {
                withContext(Dispatchers.Main) {
                    block(it)
                }
            }
        }
    }

    fun queryAllStudentInfoListAndThen(block: (List<StudentInfo>) -> Unit) {
        launch(studentInfoList) {
            val list = if (studentInfoList.value == null) {
                if (studentList.value == null) {
                    val list = studentRepository.queryAllStudentList()
                    if (list.isNullOrEmpty()) {
                        studentList.empty()
                    } else {
                        studentList.content(list)
                    }
                }
                val infoList = ArrayList<StudentInfo>()
                studentList.value?.data?.forEach {
                    try {
                        val info = studentRepository.queryStudentInfo(it, fromCache = true)
                        infoList.add(info)
                    } catch (e: Exception) {
                        Log.e(TAG, "queryAllStudentInfoListAndThen: ", e)
                    }
                }
                if (infoList.isNullOrEmpty()) {
                    studentInfoList.empty()
                } else {
                    studentInfoList.content(infoList)
                }
                infoList
            } else {
                studentInfoList.value?.data
            }
            list?.let {
                withContext(Dispatchers.Main) {
                    block(it)
                }
            }
        }
    }

    fun updateCurrentYearAndTerm() {
        viewModelScope.launch {
            ConfigUtil.getCurrentYearAndTerm(initRepository.getStartTime())
        }
    }

    fun getSchoolCalendarUrl(block: (String?) -> Unit) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e(TAG, "getSchoolCalendarUrl: ", throwable)
            block(null)
        }) {
            schoolCalendarRepository.getUrl(block)
        }
    }

    fun exportToCalendar(
        studentList: List<Student>,
        remindList: List<Int>,
        exportCustomCourse: Boolean,
        exportCustomThing: Boolean
    ) {
        launch(exportCalendar) {
            val context = context()
            withContext(Dispatchers.Default) {
                val currentYear = ConfigurationUtil.currentYear
                val currentTerm = ConfigurationUtil.currentTerm
                val startTimeArray = context.resources.getStringArray(R.array.start_time)
                val endTimeArray = context.resources.getStringArray(R.array.end_time)
                val startCalendar = initRepository.getStartDateTime()
                val hasAlarm = remindList.isNotEmpty()
                studentList.forEach { student ->
                    val accountName = "${student.username}(${currentYear}第${currentTerm}学期)@西瓜课表"
                    deleteAllEvent(context, accountName)
                    val all = ArrayList<Course>()
                    val courseList = courseRepository.queryCourseByUsernameAndTerm(
                        student,
                        currentYear,
                        currentTerm,
                        fromCache = true,
                        throwError = true
                    )
                    all.addAll(courseList)
                    if (exportCustomCourse) {
                        val customCourseList = courseRepository.queryCustomCourseByTerm(
                            student,
                            currentYear,
                            currentTerm
                        )
                        all.addAll(customCourseList)
                    }
                    all.forEach { course ->
                        val timeArray = course.time.split("-")
                        val startIndex = Integer.parseInt(timeArray[0])
                        val endIndex = Integer.parseInt(timeArray[1])
                        val startTime = startTimeArray[startIndex - 1]
                        val endTime = endTimeArray[endIndex - 1]
                        val weekArray = course.week.split("-")
                        val day = Integer.parseInt(course.day)
                        if (weekArray.size == 1) {
                            val week = Integer.parseInt(weekArray[0])
                            val date = CalendarUtil.parseMillis(week, day, startCalendar)
                            val start = CalendarUtil.parseMillis(date, startTime)
                            val end = CalendarUtil.parseMillis(date, endTime)
                            val calendarEvent = CalendarEvent(
                                course.name,
                                start,
                                end,
                                course.location,
                                "",
                                allDay = false,
                                hasAlarm = hasAlarm
                            )
                            if (course.teacher.isNotBlank())
                                calendarEvent.attendees.add(CalendarAttendee(course.teacher))
                            calendarEvent.reminder.addAll(remindList)
                            addEvent(context, accountName, calendarEvent)
                        } else {
                            val startWeek = Integer.parseInt(weekArray[0])
                            val endWeek = Integer.parseInt(weekArray[1])
                            for (week in startWeek..endWeek) {
                                val date = CalendarUtil.parseMillis(week, day, startCalendar)
                                val start = CalendarUtil.parseMillis(date, startTime)
                                val end = CalendarUtil.parseMillis(date, endTime)
                                val calendarEvent = CalendarEvent(
                                    course.name,
                                    start,
                                    end,
                                    course.location,
                                    "",
                                    allDay = false,
                                    hasAlarm = hasAlarm
                                )
                                calendarEvent.attendees.add(CalendarAttendee(course.teacher))
                                calendarEvent.reminder.addAll(remindList)
                                addEvent(context, accountName, calendarEvent)
                            }
                        }
                    }
                    if (exportCustomThing) {
                        val customThingList = customThingRepository.getAll()
                        customThingList.forEach { thing ->
                            val pair = CalendarUtil.parseCustomThingStartTime(thing)
                            val calendarEvent = CalendarEvent(
                                thing.title,
                                pair.first,
                                pair.second,
                                thing.location,
                                thing.mark,
                                allDay = thing.isAllDay,
                                hasAlarm = hasAlarm
                            )
                            calendarEvent.reminder.addAll(remindList)
                            addEvent(context, accountName, calendarEvent)
                        }
                    }
                }
                exportCalendar.content(true)
            }
        }
    }

    fun getAllCalendarAccount(listener: (List<Pair<String, Long>>) -> Unit) {
        viewModelScope.launch {
            val context = context()
            val accountList = withContext(Dispatchers.Default) {
                getAllCalendarAccount(context)
            }
            withContext(Dispatchers.Main) {
                listener(accountList)
            }
        }
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}