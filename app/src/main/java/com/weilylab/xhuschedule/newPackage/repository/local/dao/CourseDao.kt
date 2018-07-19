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

	@Query("select * from tb_course where student_id = :username")
	fun queryCourseByUsername(username: String): List<Course>
}