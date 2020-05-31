package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.api.NoticeAPI
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.model.Notice
import com.weilylab.xhuschedule.repository.local.dao.NoticeDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.tools.utils.isConnectInternet

class NoticeRepository : KoinComponent {
	private val noticeDao: NoticeDao by inject()

	private val noticeAPI: NoticeAPI by inject()

	suspend fun queryNotice(queryOnline: Boolean): Boolean {
		suspend fun queryLocal(): Boolean {
			val noticeList = noticeDao.queryNoticeByPlatform(Constants.NOTICE_PLATFORM_ANDROID)
			return noticeList.any { !it.isRead }
		}
		if (isConnectInternet() && queryOnline) {
			return withContext(Dispatchers.IO) {
				val noticeResponse = noticeAPI.getNotices(Constants.NOTICE_PLATFORM_ANDROID)
				if (noticeResponse.isSuccessful) {
					noticeResponse.notices.forEach {
						val save = noticeDao.queryNoticeById(it.id)
						if (save != null) {
							it.isRead = save.isRead
							noticeDao.update(it)
						} else
							noticeDao.add(it)
					}
					noticeResponse.notices.any { !it.isRead }
				} else {
					queryLocal()
				}
			}
		} else {
			return withContext(Dispatchers.IO) { queryLocal() }
		}
	}

	suspend fun queryNoticeForAndroid(): List<Notice> {
		suspend fun queryLocal(): List<Notice> {
			return noticeDao.queryNoticeByPlatform(Constants.NOTICE_PLATFORM_ANDROID)
		}
		if (isConnectInternet()) {
			return withContext(Dispatchers.IO) {
				val noticeResponse = noticeAPI.getNotices(Constants.NOTICE_PLATFORM_ANDROID)
				if (noticeResponse.isSuccessful) {
					val result = noticeResponse.notices
					noticeResponse.notices.forEach {
						val save = noticeDao.queryNoticeById(it.id)
						if (save != null)
							noticeDao.update(it)
						else
							noticeDao.add(it)
					}
					result
				} else {
					queryLocal()
				}
			}
		} else {
			return withContext(Dispatchers.IO) { queryLocal() }
		}
	}

	suspend fun markListAsRead(list: List<Notice>) {
		list.forEach {
			it.isRead = true
			noticeDao.update(it)
		}
	}
}