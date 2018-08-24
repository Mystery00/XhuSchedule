package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.config.Status.*
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.repository.remote.TestRemoteDataSource
import com.weilylab.xhuschedule.utils.TestUtil
import com.weilylab.xhuschedule.utils.UserUtil
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.viewModel.QueryTestViewModel

object TestRepository {
	fun queryTests(queryTestViewModel: QueryTestViewModel) {
		queryTestViewModel.testList.value = PackageData.loading()
		queryTestViewModel.testList.addSource(queryTestViewModel.studentList) { packageData ->
			when (packageData.status) {
				Content -> {
					val mainStudent = UserUtil.findMainStudent(packageData.data)
					if (mainStudent == null)
						queryTestViewModel.testList.value = PackageData.empty()
					else
						TestRemoteDataSource.queryAllTestsByUsername(queryTestViewModel.testList, mainStudent)
				}
				Error -> queryTestViewModel.testList.value = PackageData.error(packageData.error)
				Empty -> queryTestViewModel.testList.value = PackageData.empty()
				Loading -> queryTestViewModel.testList.value = PackageData.loading()
			}
		}
		StudentLocalDataSource.queryAllStudentList(queryTestViewModel.studentList)
	}

	fun queryTestsForManyStudent(queryTestViewModel: QueryTestViewModel) {
		queryTestViewModel.testList.value = PackageData.loading()
		queryTestViewModel.testList.addSource(queryTestViewModel.studentList) {
			when (it.status) {
				Content -> if (it.data!!.isNotEmpty()) TestRemoteDataSource.queryAllTestsForManyStudent(queryTestViewModel.testList, it.data)
				Error -> queryTestViewModel.testList.value = PackageData.error(it.error)
				Empty -> queryTestViewModel.testList.value = PackageData.empty()
				Loading -> queryTestViewModel.testList.value = PackageData.loading()
			}
		}
		StudentLocalDataSource.queryAllStudentList(queryTestViewModel.studentList)
	}
}