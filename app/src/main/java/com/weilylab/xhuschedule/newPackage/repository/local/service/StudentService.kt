package com.weilylab.xhuschedule.newPackage.repository.local.service

import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.StudentInfo

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