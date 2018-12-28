package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.repository.remote.TestRemoteDataSource
import com.weilylab.xhuschedule.utils.userDo.UserUtil
import com.weilylab.xhuschedule.viewModel.QueryTestViewModel
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.Status.*

object TestRepository {
	fun queryStudentList(queryTestViewModel: QueryTestViewModel) = StudentLocalDataSource.queryAllStudentList(queryTestViewModel.studentList)

	fun queryTests(queryTestViewModel: QueryTestViewModel, student: Student) {
		queryTestViewModel.testList.value = PackageData.loading()
		TestRemoteDataSource.queryAllTestsByUsername(queryTestViewModel.testList, queryTestViewModel.html, student)
	}

	fun queryTestsForManyStudent(queryTestViewModel: QueryTestViewModel) {
		queryTestViewModel.testList.value = PackageData.loading()
		queryTestViewModel.testList.removeSource(queryTestViewModel.studentList)
		queryTestViewModel.testList.addSource(queryTestViewModel.studentList) {
			when (it.status) {
				Content -> if (it.data!!.isNotEmpty()) TestRemoteDataSource.queryAllTestsForManyStudent(queryTestViewModel.testList, it.data!!)
				Error -> queryTestViewModel.testList.value = PackageData.error(it.error)
				Empty -> queryTestViewModel.testList.value = PackageData.empty()
				Loading -> queryTestViewModel.testList.value = PackageData.loading()
			}
		}
		StudentLocalDataSource.queryAllStudentList(queryTestViewModel.studentList)
	}
}