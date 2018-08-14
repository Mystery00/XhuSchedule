package com.weilylab.xhuschedule.repository

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.repository.remote.StudentRemoteDataSource
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.viewModel.LoginViewModel

object LoginRepository {
	fun login(student: Student, loginViewModel: LoginViewModel) = StudentRemoteDataSource.login(loginViewModel.loginLiveData, student)

	fun queryStudentInfo(student: Student) {
		StudentRemoteDataSource.queryStudentInfo(MutableLiveData(), student)
	}
}