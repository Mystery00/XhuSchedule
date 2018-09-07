package com.weilylab.xhuschedule.repository.local.dao

import androidx.room.*
import com.weilylab.xhuschedule.model.FeedBackMessage

@Dao
interface FeedBackMessageDao {
	@Insert
	fun insert(feedBackMessage: FeedBackMessage): Long

	@Delete
	fun delete(feedBackMessage: FeedBackMessage): Int

	@Update
	fun update(feedBackMessage: FeedBackMessage)

	@Query("select * from tb_feedback_message where sender = :username or receiver = :username")
	fun queryMessageForStudent(username: String): List<FeedBackMessage>
}