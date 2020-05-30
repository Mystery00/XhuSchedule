package com.weilylab.xhuschedule.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.weilylab.xhuschedule.api.ScoreAPI
import com.weilylab.xhuschedule.model.CetScore
import com.weilylab.xhuschedule.model.ClassScore
import com.weilylab.xhuschedule.model.ExpScore
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.module.check
import com.weilylab.xhuschedule.module.checkConnect
import com.weilylab.xhuschedule.module.redoAfterLogin
import com.weilylab.xhuschedule.repository.local.dao.ScoreDao
import org.koin.core.KoinComponent
import org.koin.core.inject

class ScoreRepository : KoinComponent {
	private val scoreDao: ScoreDao by inject()

	private val scoreAPI: ScoreAPI by inject()

	suspend fun queryClassScoreOnline(student: Student, year: String, term: String): List<ClassScore> = checkConnect {
		val response = scoreAPI.getScores(student.username, year, term).redoAfterLogin(student) {
			scoreAPI.getScores(student.username, year, term)
		}.check()
		response.scores.forEach {
			it.studentID = student.username
			it.year = year
			it.term = term
			it.failed = false
			scoreDao.saveClassScore(it)
		}
		response.failScores.forEach {
			it.studentID = student.username
			it.year = year
			it.term = term
			it.failed = true
			scoreDao.saveClassScore(it)
		}
		val list = ArrayList<ClassScore>(response.scores.size + response.failScores.size)
		list.addAll(response.scores)
		list.addAll(response.failScores)
		list
	}

	suspend fun queryClassScoreLocal(student: Student, year: String, term: String): List<ClassScore> = scoreDao.queryClassScore(student.username, year, term)

	suspend fun queryExpScoreOnline(student: Student, year: String, term: String): List<ExpScore> = checkConnect {
		val response = scoreAPI.getExpScores(student.username, year, term).redoAfterLogin(student) {
			scoreAPI.getExpScores(student.username, year, term)
		}.check()
		response.expScores.forEach {
			it.studentID = student.username
			it.year = year
			it.term = term
			scoreDao.saveExpScore(it)
		}
		response.expScores
	}

	suspend fun queryExpScoreLocal(student: Student, year: String, term: String): List<ExpScore> = scoreDao.queryExpScore(student.username, year, term)

	suspend fun getCetVCode(student: Student, no: String): Bitmap = checkConnect {
		val response = scoreAPI.getCETVCode(student.username, no, null).redoAfterLogin(student) {
			scoreAPI.getCETVCode(student.username, no, null)
		}.check()
		val bytes = Base64.decode(response.vcode.substring(response.vcode.indexOfFirst { it == ',' }), Base64.DEFAULT)
		return@checkConnect BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
	}

	suspend fun getCetScore(student: Student, no: String, name: String, vcode: String): CetScore = checkConnect {
		val response = scoreAPI.getCETScores(student.username, no, name, vcode).redoAfterLogin(student) {
			scoreAPI.getCETScores(student.username, no, name, vcode)
		}.check()
		return@checkConnect response.cetScore
	}
}