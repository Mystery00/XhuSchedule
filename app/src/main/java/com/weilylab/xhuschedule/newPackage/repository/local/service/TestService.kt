package com.weilylab.xhuschedule.newPackage.repository.local.service

import com.weilylab.xhuschedule.newPackage.model.Test

interface TestService {
	fun insert(test: Test): Long

	fun delete(test: Test): Int

	fun queryAllTest(): List<Test>

	fun queryTestsForStudent(username: String): List<Test>

	fun queryTestsOnThisDay(date: String): List<Test>
}