package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Notice
import com.weilylab.xhuschedule.repository.NoticeRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.content
import vip.mystery0.rx.launch

class NoticeViewModel : ViewModel(), KoinComponent {
	private val noticeRepository: NoticeRepository by inject()
	val noticeList by lazy { MutableLiveData<PackageData<List<Notice>>>() }

	fun queryNotice() {
		launch(noticeList) {
			noticeList.content(noticeRepository.queryNoticeForAndroid())
		}
	}

	fun markListAsRead(list: List<Notice>) {
		launch(noticeList) {
			noticeRepository.markListAsRead(list)
		}
	}
}