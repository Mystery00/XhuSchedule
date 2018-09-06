package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.repository.remote.ScoreRemoteDataSource
import com.weilylab.xhuschedule.viewModel.QueryCetScoreViewModelHelper
import com.weilylab.xhuschedule.viewModel.QueryClassScoreViewModel
import com.weilylab.xhuschedule.viewModel.QueryExpScoreViewModel
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.Status.*

object ScoreRepository {
	fun queryClassScore(scoreViewModel: QueryClassScoreViewModel) {
		scoreViewModel.scoreList.value = PackageData.loading()
		ScoreRemoteDataSource.queryClassScoreByUsername(scoreViewModel.scoreList, scoreViewModel.student.value!!, scoreViewModel.year.value!!, scoreViewModel.term.value!!)
	}

	fun queryExpScore(scoreViewModel: QueryExpScoreViewModel) {
		scoreViewModel.scoreList.value = PackageData.loading()
		ScoreRemoteDataSource.queryExpScoreByUsername(scoreViewModel.scoreList, scoreViewModel.student.value!!, scoreViewModel.year.value!!, scoreViewModel.term.value!!)
	}

	fun queryAllStudentInfo(scoreViewModel: QueryClassScoreViewModel) {
		scoreViewModel.studentInfoList.value = PackageData.loading()
		scoreViewModel.studentInfoList.addSource(scoreViewModel.studentList) {
			when (it.status) {
				Content -> if (it.data!!.isNotEmpty())
					StudentLocalDataSource.queryManyStudentInfo(scoreViewModel.studentInfoList, it.data!!)
				Error -> scoreViewModel.studentInfoList.value = PackageData.error(it.error)
				Empty -> scoreViewModel.studentInfoList.value = PackageData.empty()
				Loading -> scoreViewModel.studentInfoList.value = PackageData.loading()
			}
		}
		StudentLocalDataSource.queryAllStudentList(scoreViewModel.studentList)
	}

	fun queryAllStudentInfo(scoreViewModel: QueryExpScoreViewModel){
		scoreViewModel.studentInfoList.value = PackageData.loading()
		scoreViewModel.studentInfoList.addSource(scoreViewModel.studentList) {
			when (it.status) {
				Content -> if (it.data!!.isNotEmpty())
					StudentLocalDataSource.queryManyStudentInfo(scoreViewModel.studentInfoList, it.data!!)
				Error -> scoreViewModel.studentInfoList.value = PackageData.error(it.error)
				Empty -> scoreViewModel.studentInfoList.value = PackageData.empty()
				Loading -> scoreViewModel.studentInfoList.value = PackageData.loading()
			}
		}
		StudentLocalDataSource.queryAllStudentList(scoreViewModel.studentList)
	}

	fun queryAllStudentInfo() {
		QueryCetScoreViewModelHelper.studentInfoList.value = PackageData.loading()
		QueryCetScoreViewModelHelper.studentInfoList.addSource(QueryCetScoreViewModelHelper.studentList) {
			when (it.status) {
				Content -> if (it.data!!.isNotEmpty())
					StudentLocalDataSource.queryManyStudentInfo(QueryCetScoreViewModelHelper.studentInfoList, it.data!!)
				Error -> QueryCetScoreViewModelHelper.studentInfoList.value = PackageData.error(it.error)
				Empty -> QueryCetScoreViewModelHelper.studentInfoList.value = PackageData.empty()
				Loading -> QueryCetScoreViewModelHelper.studentInfoList.value = PackageData.loading()
			}
		}
		StudentLocalDataSource.queryAllStudentList(QueryCetScoreViewModelHelper.studentList)
	}

	fun getCetVCode(student: Student = QueryCetScoreViewModelHelper.student.value!!) {
		QueryCetScoreViewModelHelper.cetVCodeLiveData.value = PackageData.loading()
		ScoreRemoteDataSource.getCetVCode(QueryCetScoreViewModelHelper.cetVCodeLiveData, student, QueryCetScoreViewModelHelper.no.value!!)
	}

	fun getCetScore(vcode: String, student: Student = QueryCetScoreViewModelHelper.student.value!!) {
		QueryCetScoreViewModelHelper.cetScoreLiveData.value = PackageData.loading()
		ScoreRemoteDataSource.queryCetScores(QueryCetScoreViewModelHelper.cetScoreLiveData, student, QueryCetScoreViewModelHelper.no.value!!, QueryCetScoreViewModelHelper.name.value!!, vcode)
	}
}