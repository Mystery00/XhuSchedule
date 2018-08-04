package com.weilylab.xhuschedule.newPackage.repository.dataSource

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData
import java.util.*

interface InitDataSource {
	fun getStartDateTime(startDateTimeLiveDate: MutableLiveData<PackageData<Calendar>>)
}