package com.weilylab.xhuschedule.repository.local.dao

import androidx.room.*
import com.weilylab.xhuschedule.model.FeedBackMessage

@Dao
interface FeedBackMessageDao {
	@Insert
	suspend fun insert(feedBackMessage: FeedBackMessage): Long

	@Delete
	suspend fun delete(feedBackMessage: FeedBackMessage): Int

	@Update
	suspend fun update(feedBackMessage: FeedBackMessage)

	@Query("select * from tb_feedback_message where (sender = :username or receiver = :username) and origin_id > :maxId")
	suspend fun queryMessageForStudent(username: String, maxId: Int): List<FeedBackMessage>

	@Query("select max(origin_id) from tb_feedback_message where sender = :username or receiver = :username")
	suspend fun queryMaxId(username: String): Int?
}