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

	@Query("SELECT * FROM tb_student")
	fun queryAllStudentList(): List<Student>

	@Query("SELECT count(username) FROM tb_student")
	fun queryStudentSize(): Int

	@Insert
	fun saveStudentInfo(studentInfo: StudentInfo): Long

	@Query("SELECT * FROM tb_student_info WHERE student_id = :username LIMIT 1")
	fun queryStudentInfoByUsername(username: String): StudentInfo?
}