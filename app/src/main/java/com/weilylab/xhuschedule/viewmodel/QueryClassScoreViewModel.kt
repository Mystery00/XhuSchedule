/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.ClassScore
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.repository.ScoreRepository
import com.weilylab.xhuschedule.repository.StudentRepository
import com.weilylab.xhuschedule.utils.CalendarUtil
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.logs.Logs
import vip.mystery0.rx.*
import java.util.*
import kotlin.collections.HashMap

class QueryClassScoreViewModel : ViewModel(), KoinComponent {
	private val studentRepository: StudentRepository by inject()
	private val scoreRepository: ScoreRepository by inject()

	val studentList by lazy { MutableLiveData<List<Student>>() }
	val studentInfoList by lazy { MutableLiveData<Map<Student, StudentInfo?>>() }
	val scoreList by lazy { MutableLiveData<PackageData<List<ClassScore>>>() }
	val student by lazy { MutableLiveData<Student>() }
	val year by lazy { MutableLiveData<String>() }
	val term by lazy { MutableLiveData<String>() }

	fun init() {
		launch(scoreList) {
			val studentArray = studentRepository.queryAllStudentList()
			studentList.postValue(studentArray)
			val main = studentArray.find { it.isMain }
			if (main == null) {
				student.postValue(null)
				return@launch
			}
			student.postValue(main)
			val map = HashMap<Student, StudentInfo>()
			studentArray.forEach {
				try {
					val info = studentRepository.queryStudentInfo(it)
					map[it] = info
				} catch (e: Exception) {
					Logs.w(e)
				}
			}
			studentInfoList.postValue(map)
			year.postValue(CalendarUtil.getSelectArray(null).last())
			val month = Calendar.getInstance().get(Calendar.MONTH)
			term.postValue(if (month in Calendar.MARCH until Calendar.SEPTEMBER) "2" else "1")
		}
	}

	fun query(student: Student, year: String, term: String) {
		scoreList.loading()
		launch(scoreList) {
			val list = scoreRepository.queryClassScoreOnline(student, year, term)
			if (list.isNullOrEmpty()) {
				scoreList.empty()
			} else {
				scoreList.content(list)
			}
		}
	}
}