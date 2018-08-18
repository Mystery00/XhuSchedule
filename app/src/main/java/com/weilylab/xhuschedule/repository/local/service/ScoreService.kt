package com.weilylab.xhuschedule.repository.local.service

import com.weilylab.xhuschedule.model.ClassScore

interface ScoreService {
	fun saveClassScore(classScore: ClassScore): Long

	fun deleteClassScore(classScore: ClassScore): Int

	fun queryClassScore(username: String, year: String, term: String): List<ClassScore>
}