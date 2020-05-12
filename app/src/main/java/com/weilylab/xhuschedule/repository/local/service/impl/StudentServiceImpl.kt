package com.weilylab.xhuschedule.repository.local.service.impl

import com.weilylab.xhuschedule.model.FeedBackToken
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.repository.local.dao.FBTokenDao
import com.weilylab.xhuschedule.repository.local.dao.StudentDao
import com.weilylab.xhuschedule.repository.local.service.StudentService

class StudentServiceImpl(private val studentDao: StudentDao,
						 private val fbTokenDao: FBTokenDao) : StudentService {
	override fun studentLogin(student: Student): Long = studentDao.studentLogin(student)

	override fun studentLogout(student: Student): Int = studentDao.studentLogout(student)

	override fun queryAllStudentList(): List<Student> = studentDao.queryAllStudentList()

	override fun queryStudentSize(): Int = studentDao.queryStudentSize()

	override fun queryStudentByUsername(username: String): Student? = studentDao.queryStudentByUsername(username)

	override fun queryMainStudent(): Student? = studentDao.queryMainStudent()

	override fun updateStudent(student: Student) = studentDao.updateStudent(student)

	override fun saveStudentInfo(studentInfo: StudentInfo): Long = studentDao.saveStudentInfo(studentInfo)

	override fun queryStudentInfoByUsername(username: String): StudentInfo? = studentDao.queryStudentInfoByUsername(username)

	override fun queryAllStudentInfo(): List<StudentInfo> = studentDao.queryAllStudentInfo()

	override fun registerFeedBackToken(feedBackToken: FeedBackToken): Long = fbTokenDao.register(feedBackToken)

	override fun unRegisterFeedBackToken(feedBackToken: FeedBackToken): Int = fbTokenDao.unRegister(feedBackToken)

	override fun updateFeedBackToken(feedBackToken: FeedBackToken) = fbTokenDao.updateToken(feedBackToken)

	override fun queryFeedBackTokenForUsername(username: String): FeedBackToken? = fbTokenDao.queryFeedBackTokenForUsername(username)
}