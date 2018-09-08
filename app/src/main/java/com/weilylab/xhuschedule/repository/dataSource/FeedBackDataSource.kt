package com.weilylab.xhuschedule.repository.dataSource

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.FeedBackMessage
import com.weilylab.xhuschedule.model.Student
import vip.mystery0.rxpackagedata.PackageData

interface FeedBackDataSource {
	fun queryFeedBackForStudent(feedBackMessageListLiveData: MutableLiveData<PackageData<List<FeedBackMessage>>>, student: Student, feedBackToken: String, lastId: Int)
}