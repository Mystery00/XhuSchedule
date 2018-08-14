package com.weilylab.xhuschedule.repository.dataSource

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.Notice
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData

interface NoticeDataSource {
	fun queryNotice(noticeLiveData: MutableLiveData<PackageData<List<Notice>>>, platform: String?)
}