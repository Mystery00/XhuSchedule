package com.weilylab.xhuschedule.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.CetScore
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.ScoreRepository
import com.weilylab.xhuschedule.repository.StudentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.content

object QueryCetScoreViewModelHelper : KoinComponent {
	private val studentRepository: StudentRepository by inject()

	private val scoreRepository: ScoreRepository by inject()

	var no = MutableLiveData<String>()
	var name = MutableLiveData<String>()
	val student by lazy { MutableLiveData<Student>() }
	var cetVCodeLiveData = MutableLiveData<PackageData<Bitmap>>()
	var cetScoreLiveData = MutableLiveData<PackageData<CetScore>>()

	fun init(scope: CoroutineScope) {
		scope.launch {
			val mainStudent = studentRepository.queryMainStudent()
			if (mainStudent == null) {
				student.postValue(null)
				return@launch
			}
			val info = studentRepository.queryStudentInfo(mainStudent)
			mainStudent.studentName = info.name
			student.postValue(mainStudent)
		}
	}

	fun getCetVCode(scope: CoroutineScope) {
		scope.launch {
			cetVCodeLiveData.content(scoreRepository.getCetVCode(student.value!!, no.value!!))
		}
	}

	fun getCetScore(scope: CoroutineScope, cetVCodeStr: String) {
		scope.launch {
			cetScoreLiveData.content(scoreRepository.getCetScore(student.value!!, no.value!!, name.value!!, cetVCodeStr))
		}
	}
}