package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.api.NoticeAPI
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.model.Notice
import com.weilylab.xhuschedule.repository.local.dao.NoticeDao
import com.weilylab.xhuschedule.repository.remote.NoticeRemoteDataSource
import com.weilylab.xhuschedule.viewmodel.BottomNavigationViewModel
import com.weilylab.xhuschedule.viewmodel.NoticeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.rx.PackageData
import vip.mystery0.tools.utils.isConnectInternet

object NoticeRepository : KoinComponent {
	private val noticeDao: NoticeDao by inject()

	private val noticeAPI: NoticeAPI by inject()

	suspend fun queryNotice(queryOnline: Boolean): Boolean {
		fun queryLocal(): Boolean {
			val noticeList = noticeDao.queryNoticeByPlatform(Constants.NOTICE_PLATFORM_ANDROID)
			return noticeList.any { !it.isRead }
		}
		if (isConnectInternet() && queryOnline) {
			return withContext(Dispatchers.IO) {
				val noticeResponse = noticeAPI.getNotices(Constants.NOTICE_PLATFORM_ANDROID)
				if (noticeResponse.isSuccessful) {
					val result = noticeResponse.notices.any { !it.isRead }
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

	fun queryNoticeForAndroid(noticeViewModel: NoticeViewModel) {
		queryNotice(noticeViewModel, Constants.NOTICE_PLATFORM_ANDROID)
	}

	private fun queryNotice(noticeViewModel: NoticeViewModel, platform: String?) {
		noticeViewModel.noticeList.value = PackageData.loading()
		NoticeRemoteDataSource.queryNotice(noticeViewModel.noticeList, platform)
	}

	fun markNoticesAsRead(list: List<Notice>) {
		NoticeLocalDataSource.markAsReadInThread(list)
	}

	fun queryNoticeInMainActivity(bottomNavigationViewModel: BottomNavigationViewModel, isFirst: Boolean) {
		bottomNavigationViewModel.noticeList.value = PackageData.loading()
		if (isFirst)
			NoticeRemoteDataSource.queryNotice(bottomNavigationViewModel.noticeList, Constants.NOTICE_PLATFORM_ANDROID)
		else
			NoticeLocalDataSource.queryNotice(bottomNavigationViewModel.noticeList, Constants.NOTICE_PLATFORM_ANDROID)
	}
}