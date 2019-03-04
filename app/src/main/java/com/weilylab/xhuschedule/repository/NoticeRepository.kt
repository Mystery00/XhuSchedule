package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.model.Notice
import com.weilylab.xhuschedule.repository.local.NoticeLocalDataSource
import com.weilylab.xhuschedule.repository.remote.NoticeRemoteDataSource
import com.weilylab.xhuschedule.viewmodel.BottomNavigationViewModel
import com.weilylab.xhuschedule.viewmodel.NoticeViewModel
import vip.mystery0.rx.PackageData

object NoticeRepository {
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