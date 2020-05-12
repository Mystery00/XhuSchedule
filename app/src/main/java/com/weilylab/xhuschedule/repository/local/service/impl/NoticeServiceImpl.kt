package com.weilylab.xhuschedule.repository.local.service.impl

import com.weilylab.xhuschedule.model.Notice
import com.weilylab.xhuschedule.repository.local.dao.NoticeDao
import com.weilylab.xhuschedule.repository.local.service.NoticeService

class NoticeServiceImpl(private val noticeDao: NoticeDao) : NoticeService {
	override fun add(notice: Notice): Long = noticeDao.add(notice)

	override fun remove(notice: Notice): Int = noticeDao.remove(notice)

	override fun queryAllNotice(): List<Notice> = noticeDao.queryAllNotice()

	override fun queryAllReadNotice(): List<Notice> = noticeDao.queryAllReadNotice()

	override fun queryNoticeForPlatform(platform: String): List<Notice> = noticeDao.queryNoticeByPlatform(platform)

	override fun queryNoticeById(id: Int): Notice? = noticeDao.queryNoticeById(id)

	override fun update(notice: Notice) = noticeDao.update(notice)
}