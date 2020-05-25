package com.weilylab.xhuschedule.repository.local.dao

import androidx.room.*
import com.weilylab.xhuschedule.model.Course

@Dao
interface CourseDao {
	@Insert
	suspend fun addCourse(course: Course): Long

	@Delete
	suspend fun deleteCourse(course: Course): Int

	@Update
	suspend fun updateCourse(course: Course)

	@Query("select * from tb_course where student_id = :username and course_year = :year and course_term = :term and edit_type = 0")
	suspend fun queryCourseByUsernameAndTerm(username: String, year: String, term: String): List<Course>

	@Query("select * from tb_course where student_id = :username and course_year = :year and course_term = :term and edit_type = 1")
	suspend fun queryCustomCourseByTerm(username: String, year: String, term: String): List<Course>

	@Query("select * from tb_course where student_id = :username and edit_type = 1")
	suspend fun queryCustomCourseByStudent(username: String): List<Course>

	@Query("select * from tb_course where edit_type = 1")
	suspend fun queryAllCustomCourse(): List<Course>

	@Query("select * from tb_course group by course_name")
	suspend fun queryDistinctCourseByUsernameAndTerm(): List<Course>

	@Query("select * from tb_course where course_name = :name")
	suspend fun queryCourseByName(name: String): List<Course>
}