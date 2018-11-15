package com.weilylab.xhuschedule.repository.local.service.impl

import com.weilylab.xhuschedule.model.ClassScore
import com.weilylab.xhuschedule.model.ExpScore
import com.weilylab.xhuschedule.repository.local.db.DBHelper
import com.weilylab.xhuschedule.repository.local.service.ScoreService

class ScoreServiceImpl : ScoreService {
	private val scoreDao by lazy { DBHelper.db.getScoreDao() }

	override fun saveClassScore(classScore: ClassScore): Long = scoreDao.saveClassScore(classScore)

	override fun deleteClassScore(classScore: ClassScore): Int = scoreDao.deleteClassScore(classScore)

	override fun queryClassScore(username: String, year: String, term: String): List<ClassScore> = scoreDao.queryClassScore(username, year, term)

	override fun saveExpScore(expScore: ExpScore): Long = scoreDao.saveExpScore(expScore)

	override fun deleteExpScore(expScore: ExpScore): Int = scoreDao.deleteExpScore(expScore)

	override fun queryExpScore(username: String, year: String, term: String): List<ExpScore> = scoreDao.queryExpScore(username, year, term)
}