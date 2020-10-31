/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.api.CourseAPI
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.module.redoAfterLogin
import com.weilylab.xhuschedule.repository.local.dao.CourseDao
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.zhuangfei.timetable.model.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class CourseRepository : KoinComponent {
    private val courseDao: CourseDao by inject()
    private val courseAPI: CourseAPI by inject()

    private val initRepository: InitRepository by inject()

    suspend fun queryCourseByUsernameAndTerm(student: Student, year: String, term: String,
                                             fromCache: Boolean,
                                             throwError: Boolean): List<Course> = if (fromCache) {
        courseDao.queryCourseByUsernameAndTerm(student.username, year, term)
    } else {
        val response = courseAPI.getCourses(student.username, year, term).redoAfterLogin(student) {
            courseAPI.getCourses(student.username, year, term)
        }
        if (response.isSuccessful) {
            //请求成功，保存数据
            val savedList = courseDao.queryCourseByUsernameAndTerm(student.username, year, term)
            savedList.forEach {
                courseDao.deleteCourse(it)
            }
            response.courses.forEach { course ->
                course.studentID = student.username
                course.year = year
                course.term = term
                course.color = ""
                savedList.find { it.name == course.name }?.let {
                    course.color = it.color
                }
                courseDao.addCourse(course)
            }
            response.courses
        } else {
            if (throwError)
                throw Exception(response.msg)
            else
                courseDao.queryCourseByUsernameAndTerm(student.username, year, term)
        }
    }

    suspend fun queryCustomCourseByTerm(student: Student, year: String, term: String): List<Course> = courseDao.queryCustomCourseByTerm(student.username, year, term)

    suspend fun queryDistinctCourseByUsernameAndTerm(): List<Course> = courseDao.queryDistinctCourseByUsernameAndTerm()

    suspend fun updateCourseColor(course: Course, color: String) {
        courseDao.queryCourseByName(course.name).forEach {
            it.color = color
            courseDao.updateCourse(it)
        }
    }

    suspend fun getTodayCourse(courseList: List<Schedule>): List<Schedule> = withContext(Dispatchers.Default) {
        val shouldShowTomorrow = CalendarUtil.shouldShowTomorrowInfo()
        val week = if (shouldShowTomorrow) CalendarUtil.getTomorrowWeekFromCalendar(initRepository.getStartDateTime())
        else CalendarUtil.getWeekFromCalendar(initRepository.getStartDateTime())
        val weekIndex = if (shouldShowTomorrow) CalendarUtil.getTomorrowIndex()
        else CalendarUtil.getWeekIndex()
        courseList.filter { it.day == weekIndex && it.weekList.contains(week) }
                .sortedBy { it.start }
    }
}