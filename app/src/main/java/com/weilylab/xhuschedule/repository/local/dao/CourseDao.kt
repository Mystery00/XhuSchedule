package com.weilylab.xhuschedule.repository.local.dao

import androidx.room.*
import com.weilylab.xhuschedule.model.Course

@Dao
interface CourseDao {
	@Insert
	fun addCourse(course: Course): Long

	@Delete
	fun deleteCourse(course: Course): Int

	@Update
	fun updateCourse(course: Course)

	@Query("select * from tb_course where student_id = :username and course_year = :year and course_term = :term and edit_type = 0")
	fun queryCourseByUsernameAndTerm(username: String, year: String, term: String): List<Course>

	@Query("select * from tb_course where student_id = :username and course_year = :year and course_term = :term and edit_type = 1")
	fun queryCustomCourseByTerm(username: String, year: String, term: String): List<Course>

	@Query("select * from tb_course where student_id = :username and edit_type = 1")
	fun queryCustomCourseByStudent(username: String): List<Course>

	@Query("select * from tb_course where edit_type = 1")
	fun queryAllCustomCourse(): List<Course>

	@Query("select * from tb_course group by course_name")
	fun queryDistinctCourseByUsernameAndTerm(): List<Course>

	@Query("select * from tb_course where course_name = :name")
	fun queryCourseByName(name: String): List<Course>
}