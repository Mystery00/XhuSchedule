package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.repository.remote.StudentRemoteDataSource
import com.weilylab.xhuschedule.viewmodel.LoginViewModel
import java.lang.Exception

object LoginRepository {
	fun login(student: Student, loginViewModel: LoginViewModel) = StudentRemoteDataSource.login(loginViewModel.loginLiveData, student)

	fun queryStudentInfo(listener: (StudentInfo?, Exception?) -> Unit, student: Student) = StudentRemoteDataSource.queryStudentInfo(listener, student)
}