/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.api.UserAPI
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.SyncCustomCourse
import com.weilylab.xhuschedule.module.redoAfterLogin
import com.weilylab.xhuschedule.repository.local.dao.CourseDao
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.tools.ResourceException
import vip.mystery0.tools.factory.fromJson
import vip.mystery0.tools.factory.toJson
import vip.mystery0.tools.utils.isConnectInternet

class CustomCourseRepository : KoinComponent {
    private val courseDao: CourseDao by inject()

    private val userAPI: UserAPI by inject()

    suspend fun getAll(): List<Any> {
        val list = courseDao.queryAllCustomCourse()
        val map = list.groupBy { c -> c.studentID }
        val result = ArrayList<Any>()
        for (key in map.keys) {
            result.add(key)
            map.getValue(key).sortedBy { c -> c.name }.forEach { c -> result.add(c) }
        }
        return result
    }

    suspend fun save(course: Course) = courseDao.addCourse(course)

    suspend fun update(course: Course) = courseDao.updateCourse(course)

    suspend fun delete(course: Course) = courseDao.deleteCourse(course)

    suspend fun syncCustomCourseForLocal(student: Student) {
        if (isConnectInternet()) {
            val key = "customCourse"
            val response = userAPI.getUserData(student.username, key).redoAfterLogin(student) {
                userAPI.getUserData(student.username, key)
            }
            if (response.isSuccessful) {
                if (response.value.isBlank()) {
                    return
                }
                val courseList = response.value.fromJson<SyncCustomCourse>().list
                val savedList = courseDao.queryCustomCourseByStudent(student.username)
                savedList.forEach { course -> delete(course) }
                courseList.forEach { course ->
                    course.id = 0
                    course.studentID = student.username
                    save(course)
                }
                return
            } else {
                throw Exception(response.msg)
            }
        } else {
            throw ResourceException(R.string.hint_network_error)
        }
    }

    suspend fun syncCustomCourseForServer(student: Student) {
        if (isConnectInternet()) {
            val key = "customCourse"
            val localList = courseDao.queryCustomCourseByStudent(student.username)
            val value = SyncCustomCourse(localList).toJson()
            val response = userAPI.setUserData(student.username, key, value).redoAfterLogin(student) {
                userAPI.setUserData(student.username, key, value)
            }
            if (response.isSuccessful) {
                return
            } else {
                throw Exception(response.msg)
            }
        } else {
            throw ResourceException(R.string.hint_network_error)
        }
    }
}