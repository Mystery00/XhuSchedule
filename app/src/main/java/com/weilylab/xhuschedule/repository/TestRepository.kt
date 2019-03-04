package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.repository.remote.TestRemoteDataSource
import com.weilylab.xhuschedule.viewmodel.QueryTestViewModel
import vip.mystery0.rx.PackageData

object TestRepository {
	fun queryStudentList(queryTestViewModel: QueryTestViewModel) = StudentLocalDataSource.queryAllStudentList(queryTestViewModel.studentList)

	fun queryTests(queryTestViewModel: QueryTestViewModel, student: Student) {
		queryTestViewModel.testList.value = PackageData.loading()
		TestRemoteDataSource.queryAllTestsByUsername(queryTestViewModel.testList, queryTestViewModel.html, student)
	}
}