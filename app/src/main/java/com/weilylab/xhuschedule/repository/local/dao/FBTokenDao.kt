package com.weilylab.xhuschedule.repository.local.dao

import androidx.room.*
import com.weilylab.xhuschedule.model.FeedBackToken

@Dao
interface FBTokenDao {
	@Insert
	fun register(feedBackToken: FeedBackToken): Long

	@Delete
	fun unRegister(feedBackToken: FeedBackToken): Int

	@Update
	fun updateToken(feedBackToken: FeedBackToken)

	@Query("select * from tb_fb_token where username = :username limit 1")
	fun queryFeedBackTokenForUsername(username: String): FeedBackToken?
}