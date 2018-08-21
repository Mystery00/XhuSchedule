package com.weilylab.xhuschedule.repository.local.service

import com.weilylab.xhuschedule.model.ClassScore
import com.weilylab.xhuschedule.model.ExpScore

interface ScoreService {
	fun saveClassScore(classScore: ClassScore): Long

	fun deleteClassScore(classScore: ClassScore): Int

	fun queryClassScore(username: String, year: String, term: String): List<ClassScore>

	fun saveExpScore(expScore: ExpScore): Long

	fun deleteExpScore(expScore: ExpScore): Int

	fun queryExpScore(username: String, year: String, term: String): List<ExpScore>
}