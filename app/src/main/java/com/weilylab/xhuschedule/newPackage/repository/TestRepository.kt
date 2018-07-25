package com.weilylab.xhuschedule.newPackage.repository

import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.newPackage.repository.remote.TestRemoteDataSource
import com.weilylab.xhuschedule.newPackage.viewModel.QueryTestViewModel

object TestRepository {
	const val DONE = 21
	const val ERROR = 22

	fun queryTests(student: Student, queryTestViewModel: QueryTestViewModel) = TestRemoteDataSource.queryAllTests(queryTestViewModel.testList, queryTestViewModel.message, queryTestViewModel.requestCode, student)

	fun queryAllStudent(queryTestViewModel: QueryTestViewModel) = StudentLocalDataSource.queryAllStudentList(queryTestViewModel.studentList, queryTestViewModel.message, queryTestViewModel.requestCode)
}