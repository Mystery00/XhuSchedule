package com.weilylab.xhuschedule.newPackage.repository.dataSource

import androidx.lifecycle.MutableLiveData
import java.util.*

interface InitDataSource {
	fun getStartDateTime(startDateTimeLiveDate: MutableLiveData<Calendar>, weekLiveData: MutableLiveData<Int>)
}