package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.api.CourseAPI
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.module.redoAfterLogin
import com.weilylab.xhuschedule.repository.local.dao.CourseDao
import org.koin.core.KoinComponent
import org.koin.core.inject

class CourseRepository : KoinComponent {
	private val courseDao: CourseDao by inject()
	private val courseAPI: CourseAPI by inject()

	suspend fun queryCourseByUsernameAndTerm(student: Student, year: String, term: String,
											 fromCache: Boolean,
											 throwError: Boolean): List<Course> = if (fromCache) {
		courseDao.queryCourseByUsernameAndTerm(student.username, year, term)
	} else {
		val response = courseAPI.getCourses(student.username, year, term).redoAfterLogin(student) {
			courseAPI.getCourses(student.username, year, term)
		}
		if (response.isSuccessful) {
			//请求成功，返回数据
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

	}
}