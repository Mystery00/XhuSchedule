package com.weilylab.xhuschedule.newPackage.repository.local.service

import com.weilylab.xhuschedule.newPackage.model.Course

interface CourseService {
	fun addCourse(course: Course): Long

	fun deleteCourse(course: Course): Int

	fun queryCourseByUsername(username: String): List<Course>
}