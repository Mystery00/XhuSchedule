package com.weilylab.xhuschedule.newPackage.repository

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.StudentInfo
import com.weilylab.xhuschedule.newPackage.repository.remote.StudentRemoteDataSource
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.newPackage.viewModel.LoginViewModel

object LoginRepository {
	fun login(student: Student, loginViewModel: LoginViewModel) = StudentRemoteDataSource.login(loginViewModel.loginLiveData, student)

	fun queryStudentInfo(student: Student) {
		StudentRemoteDataSource.queryStudentInfo(MutableLiveData(), student)
	}
}