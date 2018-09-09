package com.weilylab.xhuschedule.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.weilylab.xhuschedule.BR
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "tb_feedback_message")
class FeedBackMessage : BaseObservable() {
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
	@get:Bindable
	var content: String = ""
		set(value) {
			field = value
			notifyPropertyChanged(BR.content)
		}
	@ColumnInfo(name = "platform")
	lateinit var platform: String
	@ColumnInfo(name = "status")
	@get:Bindable
	var status: Int = 0
		set(value) {
			field = value
			notifyPropertyChanged(BR.status)
		}

	companion object {
		@JvmStatic
		fun newLoadingMessage(username: String, content: String): FeedBackMessage {
			val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.CHINA)
			val feedBackMessage = FeedBackMessage()
			feedBackMessage.receiver = "System"
			feedBackMessage.sender = username
			feedBackMessage.content = content
			feedBackMessage.id = -1
			feedBackMessage.platform = "Android"
			feedBackMessage.status = -1//表示发送中
			feedBackMessage.createTime = simpleDateFormat.format(Calendar.getInstance().time)
			return feedBackMessage
		}
	}

	fun replace(newFeedBackMessage: FeedBackMessage) {
		dataBaseId = newFeedBackMessage.dataBaseId
		id = newFeedBackMessage.id
		receiver = newFeedBackMessage.receiver
		createTime = newFeedBackMessage.createTime
		sender = newFeedBackMessage.sender
		content = newFeedBackMessage.content
		platform = newFeedBackMessage.platform
		status = newFeedBackMessage.status
	}
}
