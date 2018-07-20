package com.weilylab.xhuschedule.newPackage.repository.local.service

import com.weilylab.xhuschedule.newPackage.model.Course

interface CourseService {
	fun addCourse(course: Course): Long

	fun deleteCourse(course: Course): Int

	fun queryCourseByUsernameAndTerm(username: String, year: String, term: String): List<Course>
}