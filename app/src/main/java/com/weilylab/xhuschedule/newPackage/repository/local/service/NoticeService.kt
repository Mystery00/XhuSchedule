package com.weilylab.xhuschedule.newPackage.repository.local.service

import com.weilylab.xhuschedule.newPackage.model.Notice

interface NoticeService {
	fun add(notice: Notice): Long

	fun remove(notice: Notice): Int

	fun queryAllNotice(): List<Notice>

	fun queryNoticeForPlatform(platform: String): List<Notice>

	fun queryNoticeById(id: Int): Notice?

	fun update(notice: Notice)
}