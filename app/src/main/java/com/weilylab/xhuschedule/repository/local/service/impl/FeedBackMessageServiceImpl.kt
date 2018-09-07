package com.weilylab.xhuschedule.repository.local.service.impl

import com.weilylab.xhuschedule.model.FeedBackMessage
import com.weilylab.xhuschedule.repository.local.db.DBHelper
import com.weilylab.xhuschedule.repository.local.service.FeedBackMessageService

class FeedBackMessageServiceImpl : FeedBackMessageService {
	private val feedBackMessageDao = DBHelper.db.getFeedBackMessageDao()

	override fun insert(feedBackMessage: FeedBackMessage): Long = feedBackMessageDao.insert(feedBackMessage)

	override fun delete(feedBackMessage: FeedBackMessage): Int = feedBackMessageDao.delete(feedBackMessage)

	override fun update(feedBackMessage: FeedBackMessage) = feedBackMessageDao.update(feedBackMessage)

	override fun queryMessageForStudent(username: String): List<FeedBackMessage> = feedBackMessageDao.queryMessageForStudent(username)
}