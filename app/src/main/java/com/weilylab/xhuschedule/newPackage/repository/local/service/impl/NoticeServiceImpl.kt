package com.weilylab.xhuschedule.newPackage.repository.local.service.impl

import com.weilylab.xhuschedule.newPackage.model.Notice
import com.weilylab.xhuschedule.newPackage.repository.local.db.DBHelper
import com.weilylab.xhuschedule.newPackage.repository.local.service.NoticeService

class NoticeServiceImpl : NoticeService {
	private val noticeDao = DBHelper.db.getNoticeDao()

	override fun add(notice: Notice): Long = noticeDao.add(notice)

	override fun remove(notice: Notice): Int = noticeDao.remove(notice)

	override fun queryAllNotice(): List<Notice> = noticeDao.queryAllNotice()

	override fun queryNoticeForPlatform(platform: String): List<Notice> = noticeDao.queryNoticeByPlatform(platform)

	override fun queryNoticeById(id: Int): Notice? = noticeDao.queryNoticeById(id)

	override fun update(notice: Notice) = noticeDao.update(notice)
}