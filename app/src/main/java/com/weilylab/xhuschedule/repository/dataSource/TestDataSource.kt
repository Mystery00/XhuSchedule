package com.weilylab.xhuschedule.repository.dataSource

import androidx.lifecycle.MediatorLiveData
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData

interface TestDataSource {
	fun queryAllTests(testLiveData: MediatorLiveData<PackageData<List<Test>>>, student: Student)
}