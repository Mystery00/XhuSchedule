package com.weilylab.xhuschedule.repository.dataSource

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import java.util.*

interface InitDataSource {
	fun getStartDateTime(startDateTimeLiveDate: MutableLiveData<PackageData<Calendar>>)
}