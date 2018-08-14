package com.weilylab.xhuschedule.repository.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.weilylab.xhuschedule.model.Test

@Dao
interface TestDao {
	@Insert
	fun insert(test: Test): Long

	@Delete
	fun delete(test: Test): Int

	@Query("select * from tb_test")
	fun queryAllTest(): List<Test>

	@Query("select * from tb_test where student_id = :username")
	fun queryTestsForStudent(username: String): List<Test>

	@Query("select * from tb_test where test_date = :date")
	fun queryTestsOnThisDay(date: String): List<Test>
}