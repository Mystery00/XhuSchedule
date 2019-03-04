package com.weilylab.xhuschedule.repository.ds

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.ClassScore
import com.weilylab.xhuschedule.model.ExpScore
import com.weilylab.xhuschedule.model.Student
import vip.mystery0.rx.PackageData

interface ScoreDataSource {
	fun queryClassScoreByUsername(scoreLiveData: MutableLiveData<PackageData<List<ClassScore>>>, student: Student, year: String, term: String)

	fun queryExpScoreByUsername(scoreLiveData: MutableLiveData<PackageData<List<ExpScore>>>, student: Student, year: String, term: String)
}