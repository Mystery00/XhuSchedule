package com.weilylab.xhuschedule.repository.dataSource

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.Notice
import vip.mystery0.rxpackagedata.PackageData

interface NoticeDataSource {
	fun queryNotice(noticeLiveData: MutableLiveData<PackageData<List<Notice>>>, platform: String?)
}