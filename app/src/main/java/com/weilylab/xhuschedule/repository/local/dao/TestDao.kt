package com.weilylab.xhuschedule.repository.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.weilylab.xhuschedule.model.Test

@Dao
interface TestDao {
	@Insert
	suspend fun insert(test: Test): Long

	@Delete
	suspend fun delete(test: Test): Int

	@Query("select * from tb_test")
	suspend fun queryAllTest(): List<Test>

	@Query("select * from tb_test where student_id = :username")
	suspend fun queryTestsForStudent(username: String): List<Test>

	@Query("select * from tb_test where test_date = :date")
	suspend fun queryTestsOnThisDay(date: String): List<Test>
}