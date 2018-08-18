package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.ClassScore
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.dataSource.ScoreDataSource
import com.weilylab.xhuschedule.repository.local.service.impl.ScoreServiceImpl
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.utils.rxAndroid.RxObserver

object ScoreLocalDataSource : ScoreDataSource {
	private val scoreService = ScoreServiceImpl()
	override fun queryClassScoreByUsername(scoreLiveData: MutableLiveData<PackageData<List<ClassScore>>>, student: Student, year: String, term: String) {
		RxObservable<List<ClassScore>>()
				.doThings {
					it.onFinish(scoreService.queryClassScore(student.username, year, term))
				}.subscribe(object : RxObserver<List<ClassScore>>() {
					override fun onFinish(data: List<ClassScore>?) {
						scoreLiveData.value = PackageData.content(data)
					}

					override fun onError(e: Throwable) {
						scoreLiveData.value = PackageData.error(e)
					}
				})
	}

	fun deleteAllClassScoreForStudent(username: String, year: String, term: String) {
		val list = scoreService.queryClassScore(username, year, term)
		list.forEach {
			scoreService.deleteClassScore(it)
		}
	}

	fun saveClassScoreList(list: List<ClassScore>) {
		list.forEach {
			scoreService.saveClassScore(it)
		}
	}
}