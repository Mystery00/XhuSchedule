package com.weilylab.xhuschedule.repository.local.service

import com.weilylab.xhuschedule.model.FeedBackMessage

interface FeedBackMessageService {
	fun insert(feedBackMessage: FeedBackMessage): Long

	fun delete(feedBackMessage: FeedBackMessage): Int

	fun update(feedBackMessage: FeedBackMessage)

	fun queryMessageForStudent(username: String): List<FeedBackMessage>
}