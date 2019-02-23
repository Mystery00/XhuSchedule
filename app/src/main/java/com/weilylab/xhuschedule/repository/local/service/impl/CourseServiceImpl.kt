package com.weilylab.xhuschedule.repository.local.service.impl

import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.repository.local.db.DBHelper
import com.weilylab.xhuschedule.repository.local.service.CourseService

class CourseServiceImpl : CourseService {
	private val courseDao by lazy { DBHelper.db.getCourseDao() }

	override fun addCourse(course: Course): Long = courseDao.addCourse(course)

	override fun deleteCourse(course: Course): Int = courseDao.deleteCourse(course)

	override fun updateCourse(course: Course) = courseDao.updateCourse(course)

	override fun queryCourseByUsernameAndTerm(username: String, year: String, term: String): List<Course> = courseDao.queryCourseByUsernameAndTerm(username, year, term)

	override fun queryCustomCourseByTerm(year: String, term: String): List<Course> = courseDao.queryCustomCourseByTerm(year, term)

	override fun queryAllCustomCourse(): List<Course> = courseDao.queryAllCustomCourse()

	override fun queryDistinctCourseByUsernameAndTerm(): List<Course> = courseDao.queryDistinctCourseByUsernameAndTerm()

	override fun queryCourseByName(name: String): List<Course> = courseDao.queryCourseByName(name)
}