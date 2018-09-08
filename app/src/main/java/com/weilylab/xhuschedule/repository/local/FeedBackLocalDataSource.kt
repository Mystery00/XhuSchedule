package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.FeedBackMessage
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.dataSource.FeedBackDataSource
import com.weilylab.xhuschedule.repository.local.service.impl.FeedBackMessageServiceImpl
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.rx.RxObservable
import vip.mystery0.rxpackagedata.rx.RxObserver

object FeedBackLocalDataSource : FeedBackDataSource {
	private val feedBackMessageService = FeedBackMessageServiceImpl()

	override fun queryFeedBackForStudent(feedBackMessageListLiveData: MutableLiveData<PackageData<List<FeedBackMessage>>>, student: Student, feedBackToken: String, lastId: Int) {
		RxObservable<List<FeedBackMessage>>()
				.doThings {
					it.onFinish(feedBackMessageService.queryMessageForStudent(student.username))
				}
				.subscribe(object : RxObserver<List<FeedBackMessage>>() {
					override fun onError(e: Throwable) {
						feedBackMessageListLiveData.value = PackageData.error(e)
					}

					override fun onFinish(data: List<FeedBackMessage>?) {
						if (data == null || data.isEmpty())
							feedBackMessageListLiveData.value = PackageData.empty()
						else
							feedBackMessageListLiveData.value = PackageData.content(data)
					}
				})
	}

	fun saveFeedBackMessage(feedBackMessageList: List<FeedBackMessage>) {
		feedBackMessageList.forEach {
			feedBackMessageService.insert(it)
		}
	}
}