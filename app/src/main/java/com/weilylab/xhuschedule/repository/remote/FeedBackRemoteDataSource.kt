package com.weilylab.xhuschedule.repository.remote

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.FeedBackMessage
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.dataSource.FeedBackDataSource
import com.weilylab.xhuschedule.utils.FeedBackUtil
import vip.mystery0.rxpackagedata.PackageData

object FeedBackRemoteDataSource : FeedBackDataSource {
	override fun queryFeedBackForStudent(feedBackMessageListLiveData: MutableLiveData<PackageData<List<FeedBackMessage>>>, student: Student, feedBackToken: String) {

	}

	fun sendFeedBackMessage(feedBackMessageListLiveData: MutableLiveData<PackageData<List<FeedBackMessage>>>, student: Student, content: String, feedBackToken: String) {
		val list = feedBackMessageListLiveData.value?.data
		if (list != null) {
			val feedBackMessage = FeedBackMessage.newLoadingMessage(student.username, content)
			list.toMutableList().add(feedBackMessage)
			feedBackMessageListLiveData.value = PackageData.content(list)
		}
		FeedBackUtil.sendFeedBackMessage(student, feedBackToken, content, object : RequestListener<Boolean> {
			override fun done(t: Boolean) {
				queryFeedBackForStudent(feedBackMessageListLiveData, student, feedBackToken)
			}

			override fun error(rt: String, msg: String?) {
				feedBackMessageListLiveData.value = PackageData.error(Exception(msg))
			}
		})
	}
}