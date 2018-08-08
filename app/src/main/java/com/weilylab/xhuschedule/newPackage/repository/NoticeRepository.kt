package com.weilylab.xhuschedule.newPackage.repository

import com.weilylab.xhuschedule.newPackage.model.Notice
import com.weilylab.xhuschedule.newPackage.repository.local.NoticeLocalDataSource
import com.weilylab.xhuschedule.newPackage.repository.remote.NoticeRemoteDataSource
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.newPackage.viewModel.BottomNavigationViewModel
import com.weilylab.xhuschedule.newPackage.viewModel.NoticeViewModel

object NoticeRepository {
	fun queryNoticeForAndroid(noticeViewModel: NoticeViewModel) {
		queryNotice(noticeViewModel, "Android")
	}

	fun queryAllNotice(noticeViewModel: NoticeViewModel) {
		queryNotice(noticeViewModel, "All")
	}

	private fun queryNotice(noticeViewModel: NoticeViewModel, platform: String?) {
		noticeViewModel.noticeList.value = PackageData.loading()
		NoticeRemoteDataSource.queryNotice(noticeViewModel.noticeList, platform)
	}

	fun markNoticesAsRead(list: List<Notice>) {
		NoticeLocalDataSource.markAsReadInThread(list)
	}

	fun queryNoticeInMainActivity(bottomNavigationViewModel: BottomNavigationViewModel) {
		bottomNavigationViewModel.noticeList.value = PackageData.loading()
		NoticeRemoteDataSource.queryNotice(bottomNavigationViewModel.noticeList, "Android")
	}
}