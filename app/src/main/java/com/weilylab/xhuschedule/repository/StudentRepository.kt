/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.api.UserAPI
import com.weilylab.xhuschedule.api.XhuScheduleCloudAPI
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.interceptor.CookieManger
import com.weilylab.xhuschedule.model.LoginParam
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.model.response.LoginResponse
import com.weilylab.xhuschedule.repository.local.dao.StudentDao
import com.weilylab.xhuschedule.utils.AESUtils
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.RSAUtil
import com.weilylab.xhuschedule.utils.generateSeed
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.tools.ResourceException
import vip.mystery0.tools.utils.isConnectInternet

class StudentRepository : KoinComponent {
    private val studentDao: StudentDao by inject()

    private val feedBackRepository: FeedBackRepository by inject()

    private val xhuScheduleCloudAPI: XhuScheduleCloudAPI by inject()
    private val userAPI: UserAPI by inject()

    suspend fun login(student: Student): Student {
        val studentList = studentDao.queryAllStudentList()
        val loggedStudent = studentList.find { logged -> logged.username == student.username }
        if (loggedStudent != null) {
            throw ResourceException(R.string.hint_student_logged)
        }
        if (!isConnectInternet()) {
            throw ResourceException(R.string.hint_network_error)
        }
        doLogin(student)
        queryStudentInfo(student, fromCache = false)
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
        var secretKey = student.key
        var plainPassword: String = student.password
        if (secretKey.isNullOrBlank()) {
            secretKey = generateSeed()
        } else {
            //如果密钥不为空，说明是加密数据，解密出原始信息
            plainPassword = AESUtils.aesDecrypt(secretKey, plainPassword)
        }

        val loginResponse: LoginResponse
        if (ConfigurationUtil.encryptPassword) {
            val publicKeyResponse = xhuScheduleCloudAPI.getPublicKey(student.username)
            if (!publicKeyResponse.isSuccessful) {
                throw ResourceException(R.string.error_get_public_key_failed)
            }
            val publicKey = publicKeyResponse.data
            val encryptPassword = RSAUtil.encryptString(plainPassword, publicKey)
            loginResponse =
                xhuScheduleCloudAPI.login(LoginParam(student.username, encryptPassword, publicKey))
        } else {
            loginResponse = userAPI.autoLogin(student.username, plainPassword)
            secretKey = null
        }
        if (loginResponse.isSuccessful) {
            //存储意见反馈的token
            CookieManger.putCookie(
                student.username,
                Constants.SERVER_HOST,
                loginResponse.data.cookie
            )
            feedBackRepository.registerFeedBackToken(student, loginResponse.data.fbToken)
            student.key = secretKey
            student.password = if (secretKey == null) plainPassword else AESUtils.aesEncrypt(
                secretKey,
                plainPassword
            )
            if (student.id == 0) {
                val save = studentDao.queryStudentByUsername(student.username)
                student.id = save?.id ?: 0
            }
            studentDao.updateStudent(student)
        } else {
            throw Exception(loginResponse.message)
        }
    }

    suspend fun queryStudentInfo(student: Student, fromCache: Boolean = true): StudentInfo {
        if (fromCache) {
            val info = studentDao.queryStudentInfoByUsername(student.username)
            if (info != null) {
                student.studentName = info.name
                return info
            }
        }
        return queryStudentInfo(student, 0)
    }

    private suspend fun queryStudentInfo(student: Student, repeatTime: Int = 0): StudentInfo {
        if (student.id == 0) {
            val save = studentDao.queryStudentByUsername(student.username)
            student.id = save?.id ?: 0
        }
        if (repeatTime > Constants.API_RETRY_TIME) {
            throw ResourceException(R.string.hint_do_too_many)
        }
        val infoResponse = xhuScheduleCloudAPI.getMe(student.username)
        if (infoResponse.isSuccessful) {
            //请求成功，保存信息
            val info = infoResponse.data
            //删除旧的所有数据
            studentDao.queryStudentInfoListByUsername(student.username).forEach {
                studentDao.deleteStudentInfo(it)
            }
            info.studentID = student.username
            studentDao.insertStudentInfo(info)
            student.studentName = info.name
            studentDao.updateStudent(student)
            return info
        } else {
            if (infoResponse.code == ResponseCodeConstants.ERROR_NOT_LOGIN_CODE) {
                doLoginOnly(student)
                return queryStudentInfo(student, repeatTime + 1)
            } else {
                throw Exception(infoResponse.message)
            }
        }
    }

    suspend fun deleteStudentList(studentList: List<Student>) {
        studentList.forEach { s ->
            studentDao.studentLogout(s)
        }
        val list = studentDao.queryAllStudentList()
        if (list.isNotEmpty() && list.none { it.isMain }) {
            val mainStudent = list[0]
            mainStudent.isMain = true
            studentDao.updateStudent(mainStudent)
        }
    }

    suspend fun queryAllStudentList(): List<Student> = studentDao.queryAllStudentList()

    suspend fun queryMainStudent(): Student? = studentDao.queryMainStudent()

    suspend fun updateStudentList(updateList: List<Student>) {
        updateList.forEach {
            if (it.id == 0) {
                val save = studentDao.queryStudentByUsername(it.username)
                it.id = save?.id ?: 0
            }
            studentDao.updateStudent(it)
        }
    }
}