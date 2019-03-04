package com.weilylab.xhuschedule.repository.ds

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.Notice
import vip.mystery0.rx.PackageData

interface NoticeDataSource {
	fun queryNotice(noticeLiveData: MutableLiveData<PackageData<List<Notice>>>, platform: String?)
}