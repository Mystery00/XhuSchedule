package com.weilylab.xhuschedule.newPackage.repository

import com.weilylab.xhuschedule.newPackage.config.Status.*
import com.weilylab.xhuschedule.newPackage.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.newPackage.repository.remote.TestRemoteDataSource
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.newPackage.viewModel.QueryTestViewModel

object TestRepository {
	fun queryTests(queryTestViewModel: QueryTestViewModel) {
		queryTestViewModel.testList.value = PackageData.loading()
		queryTestViewModel.testList.addSource(queryTestViewModel.studentList) {
			when (it.status) {
				Content -> if (it.data!!.isNotEmpty())
					TestRemoteDataSource.queryAllTests(queryTestViewModel.testList, it.data[0])
				Error -> queryTestViewModel.testList.value = PackageData.error(it.error)
				Empty -> queryTestViewModel.testList.value = PackageData.empty()
				Loading -> queryTestViewModel.testList.value = PackageData.loading()
			}
		}
		StudentLocalDataSource.queryAllStudentList(queryTestViewModel.studentList)
	}
}