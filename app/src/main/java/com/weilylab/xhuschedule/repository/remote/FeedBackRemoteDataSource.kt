package com.weilylab.xhuschedule.repository.remote

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.listener.DoSaveListener
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.FeedBackMessage
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.local.FeedBackLocalDataSource
import com.weilylab.xhuschedule.utils.FeedBackUtil
import vip.mystery0.rxpackagedata.PackageData
import java.util.ArrayList

object FeedBackRemoteDataSource {
	fun queryFeedBackForStudent(feedBackMessageListLiveData: MutableLiveData<PackageData<List<FeedBackMessage>>>, maxId: MutableLiveData<Int>, student: Student, feedBackToken: String) {
		FeedBackUtil.getFeedBackMessage(student, feedBackToken, maxId.value
				?: 0, object : DoSaveListener<List<FeedBackMessage>> {
			override fun doSave(t: List<FeedBackMessage>) {
				FeedBackLocalDataSource.saveFeedBackMessage(student.username, t)
				Thread.sleep(100)
			}
		}, object : RequestListener<List<FeedBackMessage>> {
			override fun done(t: List<FeedBackMessage>) {
				FeedBackLocalDataSource.queryFeedBackForStudent(feedBackMessageListLiveData, maxId, student)
			}

			override fun error(rt: String, msg: String?) {
				feedBackMessageListLiveData.value = PackageData.error(Exception(msg))
			}
		})
	}

	fun sendFeedBackMessage(feedBackMessageListLiveData: MutableLiveData<PackageData<List<FeedBackMessage>>>, maxId: MutableLiveData<Int>, student: Student, content: String, feedBackToken: String) {
		val list = feedBackMessageListLiveData.value?.data
		if (list != null) {
			val feedBackMessage = FeedBackMessage.newLoadingMessage(student.username, content)
			val loadingList = ArrayList<FeedBackMessage>()
			loadingList.addAll(list)
			loadingList.add(feedBackMessage)
			feedBackMessageListLiveData.value = PackageData.content(loadingList)
		}
		FeedBackUtil.sendFeedBackMessage(student, feedBackToken, content, object : RequestListener<Boolean> {
			override fun done(t: Boolean) {
				queryFeedBackForStudent(feedBackMessageListLiveData, maxId, student, feedBackToken)
			}

			override fun error(rt: String, msg: String?) {
				feedBackMessageListLiveData.value = PackageData.error(Exception(msg))
			}
		})
	}
}