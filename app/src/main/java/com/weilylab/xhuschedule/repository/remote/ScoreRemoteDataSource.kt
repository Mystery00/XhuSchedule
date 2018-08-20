package com.weilylab.xhuschedule.repository.remote

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.listener.DoSaveListener
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.ClassScore
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.dataSource.ScoreDataSource
import com.weilylab.xhuschedule.repository.local.ScoreLocalDataSource
import com.weilylab.xhuschedule.utils.NetworkUtil
import com.weilylab.xhuschedule.utils.ScoreUtil
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import vip.mystery0.logs.Logs
import java.util.ArrayList

object ScoreRemoteDataSource : ScoreDataSource {
	override fun queryClassScoreByUsername(scoreLiveData: MutableLiveData<PackageData<List<ClassScore>>>, student: Student, year: String, term: String) {
		if (NetworkUtil.isConnectInternet()) {
			ScoreUtil.getClassScore(student, year, term, object : DoSaveListener<Map<Int, List<ClassScore>>> {
				override fun doSave(t: Map<Int, List<ClassScore>>) {
					ScoreLocalDataSource.deleteAllClassScoreForStudent(student.username, year, term)
					val resultList = ArrayList<ClassScore>()
					t[ScoreUtil.TYPE_SCORE]!!.forEach {
						it.studentID = student.username
						it.year = year
						it.term = term
						it.failed = false
						resultList.add(it)
					}
					t[ScoreUtil.TYPE_FAILED]!!.forEach {
						it.studentID = student.username
						it.year = year
						it.term = term
						it.failed = true
						resultList.add(it)
					}
					ScoreLocalDataSource.saveClassScoreList(resultList)
				}
			}, object : RequestListener<Map<Int, List<ClassScore>>> {
				override fun done(t: Map<Int, List<ClassScore>>) {
					val resultList = ArrayList<ClassScore>()
					t[ScoreUtil.TYPE_SCORE]!!.forEach {
						it.studentID = student.username
						it.year = year
						it.term = term
						it.failed = false
						resultList.add(it)
					}
					t[ScoreUtil.TYPE_FAILED]!!.forEach {
						it.studentID = student.username
						it.year = year
						it.term = term
						it.failed = true
						resultList.add(it)
					}
					scoreLiveData.value = PackageData.content(resultList)
				}

				override fun error(rt: String, msg: String?) {
					scoreLiveData.value = PackageData.error(Exception(msg))
				}
			})
		} else {
			scoreLiveData.value = PackageData.error(Exception(StringConstant.hint_network_error))
			ScoreLocalDataSource.queryClassScoreByUsername(scoreLiveData, student, year, term)
		}
	}
}