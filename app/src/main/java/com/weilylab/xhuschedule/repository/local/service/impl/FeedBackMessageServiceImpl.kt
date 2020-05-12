package com.weilylab.xhuschedule.repository.local.service.impl

import com.weilylab.xhuschedule.model.FeedBackMessage
import com.weilylab.xhuschedule.repository.local.dao.FeedBackMessageDao
import com.weilylab.xhuschedule.repository.local.service.FeedBackMessageService

class FeedBackMessageServiceImpl(private val feedBackMessageDao: FeedBackMessageDao) : FeedBackMessageService {
	override fun insert(feedBackMessage: FeedBackMessage): Long = feedBackMessageDao.insert(feedBackMessage)

	override fun delete(feedBackMessage: FeedBackMessage): Int = feedBackMessageDao.delete(feedBackMessage)

	override fun update(feedBackMessage: FeedBackMessage) = feedBackMessageDao.update(feedBackMessage)

	override fun queryMessageForStudent(username: String, maxId: Int): List<FeedBackMessage> = feedBackMessageDao.queryMessageForStudent(username, maxId)

	override fun queryMaxId(username: String): Int? = feedBackMessageDao.queryMaxId(username)
}