package com.weilylab.xhuschedule.newPackage.repository.local.service.impl

import com.weilylab.xhuschedule.newPackage.model.Course
import com.weilylab.xhuschedule.newPackage.repository.local.db.DBHelper
import com.weilylab.xhuschedule.newPackage.repository.local.service.CourseService

class CourseServiceImpl : CourseService {
	private val courseDao = DBHelper.db.getCourseDao()

	override fun addCourse(course: Course): Long = courseDao.addCourse(course)

	override fun deleteCourse(course: Course): Int = courseDao.deleteCourse(course)

	override fun queryCourseByUsername(username: String): List<Course> = courseDao.queryCourseByUsername(username)
}