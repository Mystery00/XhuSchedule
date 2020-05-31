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
import com.weilylab.xhuschedule.model.FeedBackToken

@Dao
interface FBTokenDao {
	@Insert
	suspend fun register(feedBackToken: FeedBackToken): Long

	@Delete
	suspend fun unRegister(feedBackToken: FeedBackToken): Int

	@Update
	suspend fun updateToken(feedBackToken: FeedBackToken)

	@Query("select * from tb_fb_token where username = :username limit 1")
	suspend fun queryFeedBackTokenForUsername(username: String): FeedBackToken?
}