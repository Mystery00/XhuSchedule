package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.FeedBackMessage
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.local.service.FeedBackMessageService
import com.weilylab.xhuschedule.repository.local.service.impl.FeedBackMessageServiceImpl
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs
import vip.mystery0.rx.OnlyCompleteObserver
import vip.mystery0.rx.PackageData

object FeedBackLocalDataSource {
	private val feedBackMessageService: FeedBackMessageService by lazy { FeedBackMessageServiceImpl() }

	fun queryFeedBackForStudent(feedBackMessageListLiveData: MutableLiveData<PackageData<List<FeedBackMessage>>>, maxId: MutableLiveData<Int>, student: Student) {
		Observable.create<List<FeedBackMessage>> {
			it.onNext(feedBackMessageService.queryMessageForStudent(student.username, 0))
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<List<FeedBackMessage>>() {
					override fun onError(e: Throwable) {
						feedBackMessageListLiveData.value = PackageData.error(e)
					}

					override fun onFinish(data: List<FeedBackMessage>?) {
						if (data == null || data.isEmpty())
							feedBackMessageListLiveData.value = PackageData.empty()
						else {
							maxId.value = data.sortedBy { it.id }.last().id
							feedBackMessageListLiveData.value = PackageData.content(data)
						}
					}
				})
	}

	fun queryMaxId(username: String, listener: (Int) -> Unit) {
		Observable.create<Int> {
			it.onNext(feedBackMessageService.queryMaxId(username) ?: 0)
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<Int>() {
					override fun onError(e: Throwable) {
						Logs.wtfm("onError: ", e)
						listener.invoke(0)
					}

					override fun onFinish(data: Int?) {
						listener.invoke(data ?: 0)
					}
				})
	}

	fun saveFeedBackMessage(username: String, feedBackMessageList: List<FeedBackMessage>) {
		if (feedBackMessageList.isEmpty())
			return
		val list = feedBackMessageService.queryMessageForStudent(username, 0)
		feedBackMessageList.forEach { updateFeedBackMessage(it, list) }
	}

	private fun updateFeedBackMessage(feedBackMessage: FeedBackMessage, savedList: List<FeedBackMessage>) {
		savedList.forEach {
			if (it.id == feedBackMessage.id && it.createTime == feedBackMessage.createTime) {
				it.content = feedBackMessage.content
				it.status = feedBackMessage.status
				it.platform = feedBackMessage.platform
				it.sender = feedBackMessage.sender
				it.receiver = feedBackMessage.receiver
				feedBackMessageService.update(it)
				return
			}
		}
		feedBackMessageService.insert(feedBackMessage)
	}
}