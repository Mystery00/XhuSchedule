package com.weilylab.xhuschedule.newPackage.repository

import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.repository.remote.StudentRemoteDataSource
import com.weilylab.xhuschedule.newPackage.viewModel.LoginViewModel

object LoginRepository {
	fun login(student: Student, loginViewModel: LoginViewModel) = StudentRemoteDataSource.login(loginViewModel.loginLiveData, student)
}