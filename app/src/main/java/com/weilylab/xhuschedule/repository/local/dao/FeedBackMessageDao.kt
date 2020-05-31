/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

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