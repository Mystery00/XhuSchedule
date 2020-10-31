/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.zhuangfei.timetable.model.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class BottomNavigationRepository : KoinComponent {
    private val courseRepository: CourseRepository by inject()

    /**
     * 获取缓存的所有课程的列表
     * 多用户模式
     */
    suspend fun queryCoursesForManyStudent(studentList: List<Student>,
                                           fromCache: Boolean,
                                           throwError: Boolean): List<Schedule> {
        val year = withContext(Dispatchers.IO) { ConfigurationUtil.currentYear }
        val term = withContext(Dispatchers.IO) { ConfigurationUtil.currentTerm }
        return withContext(Dispatchers.Default) {
            val result = ArrayList<Schedule>()
            studentList.forEach { student ->
                val courseList = courseRepository.queryCourseByUsernameAndTerm(student, year, term, fromCache, throwError)
                val customCourseList = courseRepository.queryCustomCourseByTerm(student, year, term)
                val all = ArrayList<Course>()
                all.addAll(courseList)
                all.addAll(customCourseList)
                result.addAll(all.map { it.schedule })
            }
            result
        }
    }

    /**
     * 获取缓存的所有课程列表
     */
    suspend fun queryCourses(mainStudent: Student,
                             fromCache: Boolean,
                             throwError: Boolean): List<Schedule> {
        val year = withContext(Dispatchers.IO) { ConfigurationUtil.currentYear }
        val term = withContext(Dispatchers.IO) { ConfigurationUtil.currentTerm }
        return withContext(Dispatchers.Default) {
            val courseList = courseRepository.queryCourseByUsernameAndTerm(mainStudent, year, term, fromCache, throwError)
            val customCourseList = courseRepository.queryCustomCourseByTerm(mainStudent, year, term)
            val all = ArrayList<Course>()
            all.addAll(courseList)
            all.addAll(customCourseList)
            all.map { it.schedule }
        }
    }
}