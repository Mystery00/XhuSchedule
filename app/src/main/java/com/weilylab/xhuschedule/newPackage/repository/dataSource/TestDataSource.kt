package com.weilylab.xhuschedule.newPackage.repository.dataSource

import androidx.lifecycle.MediatorLiveData
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.Test
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData

interface TestDataSource {
	fun queryAllTests(testLiveData: MediatorLiveData<PackageData<List<Test>>>, student: Student)
}