package com.weilylab.xhuschedule.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_fb_token")
class FeedBackToken {
	@PrimaryKey
	@ColumnInfo(name = "username")
	lateinit var username: String
	@ColumnInfo(name = "fb_token")
	lateinit var fbToken: String
}