package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.api.UserAPI
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.repository.local.dao.StudentDao
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.tools.ResourceException
import vip.mystery0.tools.utils.isConnectInternet

class LoginRepository : KoinComponent {
	private val studentDao: StudentDao by inject()

	private val feedBackRepository: FeedBackRepository by inject()

	private val userAPI: UserAPI by inject()

	suspend fun login(student: Student): Student {
		val studentList = studentDao.queryAllStudentList()
		val loggedStudent = studentList.findLast { logged -> logged.username == student.username }
		if (loggedStudent != null) {
			throw ResourceException(R.string.hint_student_logged)
		}
		if (!isConnectInternet()) {
			throw ResourceException(R.string.hint_network_error)
		}
		doLogin(student)
		val info = queryStudentInfo(student)
		student.studentName = info.name
		return student
	}

	suspend fun doLogin(student: Student) {
		doLoginOnly(student)
		//存储账号信息
		val mainStudent = studentDao.queryMainStudent()
		student.isMain = mainStudent == null
		studentDao.studentLogin(student)
	}

	suspend fun doLoginOnly(student: Student) {
		val loginResponse = userAPI.autoLogin(student.username, student.password)
		if (loginResponse.isSuccessful) {
			//存储意见反馈的token
			feedBackRepository.registerFeedBackToken(student, loginResponse.fbToken)
		} else {
			throw Exception(loginResponse.msg)
		}
	}

	suspend fun queryStudentInfo(student: Student, repeatTime: Int = 0): StudentInfo {
		if (repeatTime > Constants.API_RETRY_TIME) {
			throw ResourceException(R.string.hint_do_too_many)
		}
		val info = userAPI.getInfo(student.username)
		return when (info.rt) {
			ResponseCodeConstants.DONE -> {
				//请求成功，保存信息
				studentDao.saveStudentInfo(info)
				info
			}
			ResponseCodeConstants.ERROR_NOT_LOGIN -> {
				doLoginOnly(student)
				queryStudentInfo(student, repeatTime + 1)
			}
			else -> throw Exception(info.msg)
		}
	}
}