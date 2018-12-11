package com.weilylab.xhuschedule.repository.remote

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.listener.DoSaveListener
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.CetScore
import com.weilylab.xhuschedule.model.ClassScore
import com.weilylab.xhuschedule.model.ExpScore
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.dataSource.ScoreDataSource
import com.weilylab.xhuschedule.repository.local.ScoreLocalDataSource
import com.weilylab.xhuschedule.utils.NetworkUtil
import com.weilylab.xhuschedule.utils.userDo.ScoreUtil
import vip.mystery0.rxpackagedata.PackageData
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
					if (resultList.isNotEmpty())
						scoreLiveData.value = PackageData.content(resultList)
					else
						scoreLiveData.value = PackageData.empty()
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

	override fun queryExpScoreByUsername(scoreLiveData: MutableLiveData<PackageData<List<ExpScore>>>, student: Student, year: String, term: String) {
		if (NetworkUtil.isConnectInternet()) {
			ScoreUtil.getExpScore(student, year, term, object : DoSaveListener<List<ExpScore>> {
				override fun doSave(t: List<ExpScore>) {
					ScoreLocalDataSource.deleteAllExpScoreForStudent(student.username, year, term)
					t.forEach {
						it.year = year
						it.term = term
						it.studentID = student.username
					}
					ScoreLocalDataSource.saveExpScoreList(t)
				}
			}, object : RequestListener<List<ExpScore>> {
				override fun done(t: List<ExpScore>) {
					if (t.isNotEmpty())
						scoreLiveData.value = PackageData.content(t)
					else
						scoreLiveData.value = PackageData.empty()
				}

				override fun error(rt: String, msg: String?) {
					scoreLiveData.value = PackageData.error(Exception(msg))
				}
			})
		} else {
			scoreLiveData.value = PackageData.error(Exception(StringConstant.hint_network_error))
			ScoreLocalDataSource.queryExpScoreByUsername(scoreLiveData, student, year, term)
		}
	}

	fun getCetVCode(cetVCodeLiveData: MutableLiveData<PackageData<Bitmap>>, student: Student, no: String) {
		if (NetworkUtil.isConnectInternet()) {
			ScoreUtil.getCetVCode(student, no, object : RequestListener<Bitmap> {
				override fun done(t: Bitmap) {
					cetVCodeLiveData.value = PackageData.content(t)
				}

				override fun error(rt: String, msg: String?) {
					cetVCodeLiveData.value = PackageData.error(Exception(msg))
				}
			})
		} else {
			cetVCodeLiveData.value = PackageData.error(Exception(StringConstant.hint_network_error))
		}
	}

	fun queryCetScores(cetScoreLiveData: MutableLiveData<PackageData<CetScore>>, student: Student, no: String, name: String, vcode: String) {
		if (NetworkUtil.isConnectInternet()) {
			ScoreUtil.getCetScores(student, no, name, vcode, object : RequestListener<CetScore> {
				override fun done(t: CetScore) {
					cetScoreLiveData.value = PackageData.content(t)
				}

				override fun error(rt: String, msg: String?) {
					cetScoreLiveData.value = PackageData.error(Exception(msg))
				}
			})
		} else {
			cetScoreLiveData.value = PackageData.error(Exception(StringConstant.hint_network_error))
		}
	}
}