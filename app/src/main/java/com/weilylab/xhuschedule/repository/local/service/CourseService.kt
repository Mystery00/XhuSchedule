package com.weilylab.xhuschedule.repository.local.service

import com.weilylab.xhuschedule.model.Course

interface CourseService {
	fun addCourse(course: Course): Long

	fun deleteCourse(course: Course): Int

	fun updateCourse(course: Course)

	fun queryCourseByUsernameAndTerm(username: String, year: String, term: String): List<Course>

	fun queryCustomCourseByTerm(username: String, year: String, term: String): List<Course>

	fun queryCustomCourseByStudent(username: String): List<Course>

	fun queryAllCustomCourse(): List<Course>

	fun queryDistinctCourseByUsernameAndTerm(): List<Course>

	fun queryCourseByName(name: String): List<Course>
}