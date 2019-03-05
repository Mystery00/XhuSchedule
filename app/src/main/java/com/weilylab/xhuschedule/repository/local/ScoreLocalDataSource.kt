package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.ClassScore
import com.weilylab.xhuschedule.model.ExpScore
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.ds.ScoreDataSource
import com.weilylab.xhuschedule.repository.local.service.ScoreService
import com.weilylab.xhuschedule.repository.local.service.impl.ScoreServiceImpl
import com.weilylab.xhuschedule.utils.RxObservable
import com.weilylab.xhuschedule.utils.RxObserver
import vip.mystery0.rx.PackageData

object ScoreLocalDataSource : ScoreDataSource {
	private val scoreService: ScoreService by lazy { ScoreServiceImpl() }
	override fun queryClassScoreByUsername(scoreLiveData: MutableLiveData<PackageData<List<ClassScore>>>, student: Student, year: String, term: String) {
		RxObservable<List<ClassScore>>()
				.io {
					it.onFinish(scoreService.queryClassScore(student.username, year, term))
				}.subscribe(object : RxObserver<List<ClassScore>>() {
					override fun onFinish(data: List<ClassScore>?) {
						if (data != null && data.isNotEmpty())
							scoreLiveData.value = PackageData.content(data)
						else
							scoreLiveData.value = PackageData.empty()
					}

					override fun onError(e: Throwable) {
						scoreLiveData.value = PackageData.error(e)
					}
				})
	}

	override fun queryExpScoreByUsername(scoreLiveData: MutableLiveData<PackageData<List<ExpScore>>>, student: Student, year: String, term: String) {
		RxObservable<List<ExpScore>>()
				.io {
					it.onFinish(scoreService.queryExpScore(student.username, year, term))
				}.subscribe(object : RxObserver<List<ExpScore>>() {
					override fun onFinish(data: List<ExpScore>?) {
						if (data != null && data.isNotEmpty())
							scoreLiveData.value = PackageData.content(data)
						else
							scoreLiveData.value = PackageData.empty()
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

	fun deleteAllExpScoreForStudent(username: String, year: String, term: String) {
		val list = scoreService.queryExpScore(username, year, term)
		list.forEach {
			scoreService.deleteExpScore(it)
		}
	}

	fun saveExpScoreList(list: List<ExpScore>) {
		list.forEach {
			scoreService.saveExpScore(it)
		}
	}
}