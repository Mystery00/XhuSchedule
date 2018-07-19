package com.weilylab.xhuschedule.newPackage.repository

import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.repository.remote.StudentRemoteDataSource
import com.weilylab.xhuschedule.newPackage.viewModel.LoginViewModel

object LoginRepository {
	const val DONE = 21
	const val ERROR = 22

	fun login(student: Student, loginViewModel: LoginViewModel) = StudentRemoteDataSource.login(loginViewModel.message, loginViewModel.requestResult, student)
}