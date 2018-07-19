package com.weilylab.xhuschedule.newPackage.repository.local.service

import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.StudentInfo
import com.weilylab.xhuschedule.newPackage.repository.local.db.DBHelper

class StudentServiceImpl : StudentService {

	private val studentDao = DBHelper.db.getStudentDao()

	override fun studentLogin(student: Student): Long = studentDao.studentLogin(student)

	override fun studentLogout(student: Student): Int = studentDao.studentLogout(student)

	override fun queryAllStudentList(): List<Student> = studentDao.queryAllStudentList()

	override fun queryStudentSize(): Int = studentDao.queryStudentSize()

	override fun saveStudentInfo(studentInfo: StudentInfo): Long = studentDao.saveStudentInfo(studentInfo)

	override fun queryStudentInfoByUsername(username: String): StudentInfo? = studentDao.queryStudentInfoByUsername(username)

}