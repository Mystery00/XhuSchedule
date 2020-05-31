package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.api.TestAPI
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.module.check
import com.weilylab.xhuschedule.module.redoAfterLogin
import com.weilylab.xhuschedule.repository.local.dao.TestDao
import org.koin.core.KoinComponent
import org.koin.core.inject

class TestRepository : KoinComponent {
	private val testAPI: TestAPI by inject()

	private val testDao: TestDao by inject()

	suspend fun queryTests(student: Student): Pair<List<Test>, String> {
		val response = testAPI.getTests(student.username).redoAfterLogin(student) {
			testAPI.getTests(student.username)
		}.check()
		response.tests.forEach {
			testDao.insert(it)
		}
		return response.tests to response.html
	}

	suspend fun queryAll(student: Student): List<Test> = testDao.queryTestsForStudent(student.username)
}