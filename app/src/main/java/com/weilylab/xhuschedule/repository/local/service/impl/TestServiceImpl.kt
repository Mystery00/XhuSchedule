package com.weilylab.xhuschedule.repository.local.service.impl

import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.repository.local.dao.TestDao
import com.weilylab.xhuschedule.repository.local.service.TestService

class TestServiceImpl(private val testDao: TestDao) : TestService {
	override fun insert(test: Test): Long = testDao.insert(test)

	override fun delete(test: Test): Int = testDao.delete(test)

	override fun queryAllTest(): List<Test> = testDao.queryAllTest()

	override fun queryTestsForStudent(username: String): List<Test> = testDao.queryTestsForStudent(username)

	override fun queryTestsOnThisDay(date: String): List<Test> = testDao.queryTestsOnThisDay(date)
}