package com.weilylab.xhuschedule.newPackage.repository.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.weilylab.xhuschedule.newPackage.model.Course

@Dao
interface CourseDao {
	@Insert
	fun addCourse(course: Course): Long

	@Delete
	fun deleteCourse(course: Course): Int

	@Query("select * from tb_course where student_id = :username and course_year = :year and course_term = :term")
	fun queryCourseByUsernameAndTerm(username: String, year: String, term: String): List<Course>
}