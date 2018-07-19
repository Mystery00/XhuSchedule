package com.weilylab.xhuschedule.newPackage.repository

import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.newPackage.repository.remote.StudentRemoteDataSource
import com.weilylab.xhuschedule.newPackage.viewModel.BottomNavigationViewModel

object BottomNavigationRepository {
	const val DONE = 21
	const val ERROR = 22

	fun queryAllStudent(bottomNavigationViewModel: BottomNavigationViewModel) = StudentLocalDataSource.queryAllStudentList(bottomNavigationViewModel.studentList, bottomNavigationViewModel.message, bottomNavigationViewModel.requestCode)

	fun queryStudentInfo(student: Student, bottomNavigationViewModel: BottomNavigationViewModel) = StudentRemoteDataSource.queryStudentInfo(bottomNavigationViewModel.studentInfo, bottomNavigationViewModel.message, bottomNavigationViewModel.requestCode, student)
}