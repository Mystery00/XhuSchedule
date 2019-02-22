package com.weilylab.xhuschedule.repository.ds

import androidx.lifecycle.MutableLiveData
import vip.mystery0.rxpackagedata.PackageData
import java.util.*

interface InitDataSource {
	fun getStartDateTime(startDateTimeLiveDate: MutableLiveData<PackageData<Calendar>>)
}