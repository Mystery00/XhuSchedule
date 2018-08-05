package com.weilylab.xhuschedule.newPackage.repository.dataSource

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.model.Notice
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData

interface NoticeDataSource {
	fun queryNotice(noticeLiveData: MutableLiveData<PackageData<List<Notice>>>, platform: String?)
}