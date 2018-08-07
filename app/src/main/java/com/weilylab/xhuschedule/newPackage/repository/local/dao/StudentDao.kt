package com.weilylab.xhuschedule.newPackage.repository.local.dao

import androidx.room.*
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

	@Query("select * from tb_student where username = :username limit 1")
	fun queryStudentByUsername(username: String): Student?

	@Query("select * from tb_student where is_main = 1 limit 1")
	fun queryMainStudent(): Student?

	@Update
	fun updateStudent(student: Student)

	@Insert
	fun saveStudentInfo(studentInfo: StudentInfo): Long

	@Query("select * from tb_student_info where student_id = :username limit 1")
	fun queryStudentInfoByUsername(username: String): StudentInfo?
}