package com.weilylab.xhuschedule.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "tb_feedback_message")
class FeedBackMessage {
	/**
	 * receiver : System
	 * createTime : 2018-09-07 12:36:08.0
	 * sender : 3120150905503
	 * id : 28
	 * content : 444
	 * platform : a
	 * status : 0
	 */
	@PrimaryKey(autoGenerate = true)
	var dataBaseId = 0

	@ColumnInfo(name = "origin_id")
	var id = 0
	@ColumnInfo(name = "receiver")
	lateinit var receiver: String
	@ColumnInfo(name = "create_time")
	lateinit var createTime: String
	@ColumnInfo(name = "sender")
	lateinit var sender: String
	@ColumnInfo(name = "content")
	lateinit var content: String
	@ColumnInfo(name = "platform")
	lateinit var platform: String
	@ColumnInfo(name = "status")
	var status: Int = 0

	companion object {
		@JvmStatic
		fun newLoadingMessage(username: String, content: String): FeedBackMessage {
			val feedBackMessage = FeedBackMessage()
			feedBackMessage.receiver = "System"
			feedBackMessage.sender = username
			feedBackMessage.content = content
			feedBackMessage.id = -1
			feedBackMessage.platform = "Android"
			feedBackMessage.status = -1//表示发送中
			feedBackMessage.createTime = Calendar.getInstance().time.toString()
			return feedBackMessage
		}
	}
}
