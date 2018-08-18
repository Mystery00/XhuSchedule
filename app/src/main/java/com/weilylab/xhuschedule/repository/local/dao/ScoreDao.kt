package com.weilylab.xhuschedule.repository.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.weilylab.xhuschedule.model.ClassScore

@Dao
interface ScoreDao {
	@Insert
	fun saveClassScore(classScore: ClassScore): Long

	@Delete
	fun deleteClassScore(classScore: ClassScore): Int

	@Query("select * from tb_class_score where student_id = :username and score_year = :year and score_term = :term")
	fun queryClassScore(username: String, year: String, term: String): List<ClassScore>
}