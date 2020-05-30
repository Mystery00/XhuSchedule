package com.weilylab.xhuschedule.repository.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.weilylab.xhuschedule.model.ClassScore
import com.weilylab.xhuschedule.model.ExpScore

@Dao
interface ScoreDao {
	@Insert
	suspend fun saveClassScore(classScore: ClassScore): Long

	@Delete
	suspend fun deleteClassScore(classScore: ClassScore): Int

	@Query("select * from tb_class_score where student_id = :username and score_year = :year and score_term = :term")
	suspend fun queryClassScore(username: String, year: String, term: String): List<ClassScore>

	@Insert
	suspend fun saveExpScore(expScore: ExpScore): Long

	@Delete
	suspend fun deleteExpScore(expScore: ExpScore): Int

	@Query("select * from tb_exp_score where student_id = :username and score_year = :year and score_term = :term")
	suspend fun queryExpScore(username: String, year: String, term: String): List<ExpScore>
}