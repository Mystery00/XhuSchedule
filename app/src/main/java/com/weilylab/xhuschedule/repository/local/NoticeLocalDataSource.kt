package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.Notice
import com.weilylab.xhuschedule.repository.ds.NoticeDataSource
import com.weilylab.xhuschedule.repository.local.service.NoticeService
import com.weilylab.xhuschedule.repository.local.service.impl.NoticeServiceImpl
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.rx.DoNothingObserver
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.StartAndCompleteObserver

object NoticeLocalDataSource : NoticeDataSource {
	private val noticeService: NoticeService by lazy { NoticeServiceImpl() }
	override fun queryNotice(noticeLiveData: MutableLiveData<PackageData<List<Notice>>>, platform: String?) {
		Observable.create<List<Notice>> {
			if (platform == null)
				it.onNext(noticeService.queryAllNotice())
			else
				it.onNext(noticeService.queryNoticeForPlatform(platform))
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : StartAndCompleteObserver<List<Notice>>() {
					override fun onError(e: Throwable) {
						noticeLiveData.value = PackageData.error(e)
					}

					override fun onFinish(data: List<Notice>?) {
						if (data != null && data.isNotEmpty())
							noticeLiveData.value = PackageData.content(data)
						else
							noticeLiveData.value = PackageData.empty()
					}

					override fun onSubscribe(d: Disposable) {
						noticeLiveData.value = PackageData.loading()
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
		Observable.create<Boolean> {
			markAsRead(list)
			it.onComplete()
		}
				.subscribeOn(Schedulers.single())
				.subscribe(DoNothingObserver<Boolean>())
	}
}