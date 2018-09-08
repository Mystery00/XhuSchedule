package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.FeedBackMessage
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.local.service.impl.FeedBackMessageServiceImpl
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.rx.RxObservable
import vip.mystery0.rxpackagedata.rx.RxObserver

object FeedBackLocalDataSource {
	private val feedBackMessageService = FeedBackMessageServiceImpl()

	fun queryFeedBackForStudent(feedBackMessageListLiveData: MutableLiveData<PackageData<List<FeedBackMessage>>>, maxId: MutableLiveData<Int>, student: Student) {
		RxObservable<List<FeedBackMessage>>()
				.doThings {
					it.onFinish(feedBackMessageService.queryMessageForStudent(student.username, 0))
				}
				.subscribe(object : RxObserver<List<FeedBackMessage>>() {
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

	fun saveFeedBackMessage(username: String, feedBackMessageList: List<FeedBackMessage>) {
		if (feedBackMessageList.isEmpty())
			return
		val list = feedBackMessageService.queryMessageForStudent(username, 0)
		feedBackMessageList.forEach { updateFeedBackMessage(it, list) }
	}

	fun updateFeedBackMessage(feedBackMessage: FeedBackMessage, savedList: List<FeedBackMessage>) {
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