package com.weilylab.xhuschedule.repository.remote

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.listener.DoSaveListener
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.FeedBackMessage
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.dataSource.FeedBackDataSource
import com.weilylab.xhuschedule.repository.local.FeedBackLocalDataSource
import com.weilylab.xhuschedule.utils.FeedBackUtil
import vip.mystery0.logs.Logs
import vip.mystery0.rxpackagedata.PackageData
import java.util.ArrayList

object FeedBackRemoteDataSource : FeedBackDataSource {
	override fun queryFeedBackForStudent(feedBackMessageListLiveData: MutableLiveData<PackageData<List<FeedBackMessage>>>, student: Student, feedBackToken: String, lastId: Int) {
		FeedBackUtil.getFeedBackMessage(student, feedBackToken, lastId, object : DoSaveListener<List<FeedBackMessage>> {
			override fun doSave(t: List<FeedBackMessage>) {
				FeedBackLocalDataSource.saveFeedBackMessage(t)
			}
		}, object : RequestListener<List<FeedBackMessage>> {
			override fun done(t: List<FeedBackMessage>) {
				if (t.isEmpty())
					feedBackMessageListLiveData.value = PackageData.empty()
				else
					feedBackMessageListLiveData.value = PackageData.content(t)
			}

			override fun error(rt: String, msg: String?) {
				feedBackMessageListLiveData.value = PackageData.error(Exception(msg))
			}
		})
	}

	fun sendFeedBackMessage(feedBackMessageListLiveData: MutableLiveData<PackageData<List<FeedBackMessage>>>, student: Student, content: String, feedBackToken: String) {
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
				queryFeedBackForStudent(feedBackMessageListLiveData, student, feedBackToken, 0)
			}

			override fun error(rt: String, msg: String?) {
				feedBackMessageListLiveData.value = PackageData.error(Exception(msg))
			}
		})
	}
}