package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.Notice
import com.weilylab.xhuschedule.repository.ds.NoticeDataSource
import com.weilylab.xhuschedule.repository.local.service.NoticeService
import com.weilylab.xhuschedule.repository.local.service.impl.NoticeServiceImpl
import com.weilylab.xhuschedule.utils.DoNothingObserver
import com.weilylab.xhuschedule.utils.RxObservable
import com.weilylab.xhuschedule.utils.RxObserver
import vip.mystery0.rx.PackageData

object NoticeLocalDataSource : NoticeDataSource {
	private val noticeService: NoticeService by lazy { NoticeServiceImpl() }
	override fun queryNotice(noticeLiveData: MutableLiveData<PackageData<List<Notice>>>, platform: String?) {
		noticeLiveData.value = PackageData.loading()
		RxObservable<List<Notice>>()
				.io {
					if (platform == null)
						it.onFinish(noticeService.queryAllNotice())
					else
						it.onFinish(noticeService.queryNoticeForPlatform(platform))
				}
				.subscribe(object : RxObserver<List<Notice>>() {
					override fun onFinish(data: List<Notice>?) {
						if (data != null && data.isNotEmpty())
							noticeLiveData.value = PackageData.content(data)
						else
							noticeLiveData.value = PackageData.empty()
					}

					override fun onError(e: Throwable) {
						noticeLiveData.value = PackageData.error(e)
					}
				})
	}

	fun saveNotice(list: List<Notice>) {
		list.forEach {
			val savedNotice = noticeService.queryNoticeById(it.id)
			if (savedNotice != null) {
				it.isRead = savedNotice.isRead
				noticeService.update(it)
			} else
				noticeService.add(it)
		}
	}

	private fun markAsRead(list: List<Notice>) {
		list.forEach {
			it.isRead = true
			noticeService.update(it)
		}
	}

	fun markAsReadInThread(list: List<Notice>) {
		RxObservable<Boolean>()
				.doThingsOnThread {
					markAsRead(list)
					it.onFinish(true)
				}
				.subscribe(DoNothingObserver<Boolean>())
	}
}