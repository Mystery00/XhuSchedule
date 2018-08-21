package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.Notice
import com.weilylab.xhuschedule.repository.dataSource.NoticeDataSource
import com.weilylab.xhuschedule.repository.local.service.NoticeService
import com.weilylab.xhuschedule.repository.local.service.impl.NoticeServiceImpl
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.utils.rxAndroid.RxObserver
import vip.mystery0.logs.Logs

object NoticeLocalDataSource : NoticeDataSource {
	private val noticeService: NoticeService = NoticeServiceImpl()
	override fun queryNotice(noticeLiveData: MutableLiveData<PackageData<List<Notice>>>, platform: String?) {
		noticeLiveData.value = PackageData.loading()
		RxObservable<List<Notice>>()
				.doThings {
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

	fun queryAllReadNotices(): List<Notice> = noticeService.queryAllReadNotice()

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
				.subscribe(object : RxObserver<Boolean>() {
					override fun onFinish(data: Boolean?) {
						Logs.i("onFinish: markAsReadInThread")
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
					}
				})
	}
}