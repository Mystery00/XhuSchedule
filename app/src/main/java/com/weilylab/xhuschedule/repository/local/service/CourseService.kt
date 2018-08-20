package com.weilylab.xhuschedule.repository.local.service

import com.weilylab.xhuschedule.model.Course

interface CourseService {
	fun addCourse(course: Course): Long

	fun deleteCourse(course: Course): Int

	fun queryCourseByUsernameAndTerm(username: String, year: String, term: String): List<Course>
}