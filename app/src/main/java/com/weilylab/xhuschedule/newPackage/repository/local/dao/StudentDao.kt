package com.weilylab.xhuschedule.newPackage.repository.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.StudentInfo

@Dao
interface StudentDao {
	@Insert
	fun studentLogin(student: Student): Long

	@Delete
	fun studentLogout(student: Student): Int

	@Query("select * from tb_student")
	fun queryAllStudentList(): List<Student>

	@Query("select count(username) from tb_student")
	fun queryStudentSize(): Int

	@Insert
	fun saveStudentInfo(studentInfo: StudentInfo): Long

	@Query("select * from tb_student_info where student_id = :username limit 1")
	fun queryStudentInfoByUsername(username: String): StudentInfo?
}