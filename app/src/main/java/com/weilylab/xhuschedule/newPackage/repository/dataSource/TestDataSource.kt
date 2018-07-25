package com.weilylab.xhuschedule.newPackage.repository.dataSource

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.Test

interface TestDataSource {
	fun queryAllTests(testLiveData: MutableLiveData<List<Test>>, messageLiveData: MutableLiveData<String>, requestCodeLiveData: MutableLiveData<Int>,student: Student)
}