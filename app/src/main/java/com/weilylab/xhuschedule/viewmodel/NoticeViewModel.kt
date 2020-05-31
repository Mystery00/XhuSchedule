package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Notice
import com.weilylab.xhuschedule.repository.NoticeRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.content
import vip.mystery0.rx.empty
import vip.mystery0.rx.launch

class NoticeViewModel : ViewModel(), KoinComponent {
	private val noticeRepository: NoticeRepository by inject()
	val noticeList by lazy { MutableLiveData<PackageData<List<Notice>>>() }

	fun queryNotice() {
		launch(noticeList) {
			val list = noticeRepository.queryNoticeForAndroid()
			if (list.isNullOrEmpty()) {
				noticeList.empty()
			} else {
				noticeList.content(list)
			}
		}
	}

	fun markListAsRead(list: List<Notice>) {
		launch(noticeList) {
			noticeRepository.markListAsRead(list)
		}
	}
}