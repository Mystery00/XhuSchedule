package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.config.Status.*
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.repository.remote.ScoreRemoteDataSource
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.viewModel.QueryClassScoreViewModel

object ScoreRepository {
	fun queryClassScore(scoreViewModel: QueryClassScoreViewModel) {
		scoreViewModel.scoreList.value = PackageData.loading()
		ScoreRemoteDataSource.queryClassScoreByUsername(scoreViewModel.scoreList, scoreViewModel.student.value!!, scoreViewModel.year.value!!, scoreViewModel.term.value!!)
	}

	fun queryAllStudentInfo(scoreViewModel: QueryClassScoreViewModel) {
		scoreViewModel.studentInfoList.value = PackageData.loading()
		scoreViewModel.studentInfoList.addSource(scoreViewModel.studentList) {
			when (it.status) {
				Content -> if (it.data!!.isNotEmpty())
					StudentLocalDataSource.queryManyStudentInfo(scoreViewModel.studentInfoList, it.data)
				Error -> scoreViewModel.studentInfoList.value = PackageData.error(it.error)
				Empty -> scoreViewModel.studentInfoList.value = PackageData.empty()
				Loading -> scoreViewModel.studentInfoList.value = PackageData.loading()
			}
		}
		StudentLocalDataSource.queryAllStudentList(scoreViewModel.studentList)
	}
}