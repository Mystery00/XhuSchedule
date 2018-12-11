package com.weilylab.xhuschedule.repository.dataSource

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import vip.mystery0.rxpackagedata.PackageData

interface TestDataSource {
	fun queryAllTestsByUsername(testLiveData: MediatorLiveData<PackageData<List<Test>>>, htmlLiveData: MutableLiveData<String>?, student: Student)

	fun queryAllTestsForManyStudent(testLiveData: MediatorLiveData<PackageData<List<Test>>>, studentList: List<Student>)
}