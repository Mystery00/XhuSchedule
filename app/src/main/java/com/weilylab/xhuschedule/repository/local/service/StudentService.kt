package com.weilylab.xhuschedule.repository.local.service

import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo

interface StudentService {
	fun studentLogin(student: Student): Long

	fun studentLogout(student: Student): Int

	fun queryAllStudentList(): List<Student>

	fun queryStudentSize(): Int

	fun queryStudentByUsername(username: String):Student?

	fun queryMainStudent():Student?

	fun updateStudent(student: Student)

	fun saveStudentInfo(studentInfo: StudentInfo): Long

	fun queryStudentInfoByUsername(username: String): StudentInfo?
}