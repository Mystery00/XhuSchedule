/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.repository.local.dao

import androidx.room.*
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo

@Dao
interface StudentDao {
	@Insert
	suspend fun studentLogin(student: Student): Long

	@Delete
	suspend fun studentLogout(student: Student): Int

	@Query("select * from tb_student")
	suspend fun queryAllStudentList(): List<Student>

	@Query("select count(username) from tb_student")
	suspend fun queryStudentSize(): Int

	@Query("select * from tb_student where username = :username limit 1")
	suspend fun queryStudentByUsername(username: String): Student?

	@Query("select * from tb_student where is_main = 1 limit 1")
	suspend fun queryMainStudent(): Student?

	@Update
	suspend fun updateStudent(student: Student)

	@Insert
	suspend fun insertStudentInfo(studentInfo: StudentInfo): Long

	@Delete
	suspend fun deleteStudentInfo(studentInfo: StudentInfo)

	@Query("select * from tb_student_info where student_id = :username")
	suspend fun queryStudentInfoListByUsername(username: String): List<StudentInfo>

	@Query("select * from tb_student_info where student_id = :username limit 1")
	suspend fun queryStudentInfoByUsername(username: String): StudentInfo?

	@Query("select tb_student_info.* from tb_student join tb_student_info on tb_student.username = tb_student_info.student_id")
	suspend fun queryAllStudentInfo(): List<StudentInfo>
}