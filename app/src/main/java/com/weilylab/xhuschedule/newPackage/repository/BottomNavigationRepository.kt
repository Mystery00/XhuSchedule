package com.weilylab.xhuschedule.newPackage.repository

import com.weilylab.xhuschedule.newPackage.config.Status
import com.weilylab.xhuschedule.newPackage.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.newPackage.repository.remote.InitRemoteDataSource
import com.weilylab.xhuschedule.newPackage.repository.remote.StudentRemoteDataSource
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.newPackage.viewModel.BottomNavigationViewModel

object BottomNavigationRepository {
	const val ACTION_NONE = 30
	const val ACTION_REFRESH = 31

	fun queryStudentInfo(bottomNavigationViewModel: BottomNavigationViewModel) {
		bottomNavigationViewModel.studentInfo.value = PackageData.loading()
		bottomNavigationViewModel.studentInfo.addSource(bottomNavigationViewModel.studentList) {
			when (it.status) {
				Status.Content -> if (it.data!!.isNotEmpty())
					StudentRemoteDataSource.queryStudentInfo(bottomNavigationViewModel.studentInfo, it.data[0])
				Status.Error -> bottomNavigationViewModel.studentInfo.value = PackageData.error(it.error)
				Status.Empty -> bottomNavigationViewModel.studentInfo.value = PackageData.empty()
				Status.Loading -> bottomNavigationViewModel.studentInfo.value = PackageData.loading()
			}
		}
		StudentLocalDataSource.queryAllStudentList(bottomNavigationViewModel.studentList)
	}

	fun queryCurrentWeek(bottomNavigationViewModel: BottomNavigationViewModel) {
		InitRemoteDataSource.getStartDateTime(bottomNavigationViewModel.startDateTime, bottomNavigationViewModel.currentWeek)
	}
}