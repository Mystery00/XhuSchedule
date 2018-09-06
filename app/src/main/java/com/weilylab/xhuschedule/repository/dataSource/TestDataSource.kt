package com.weilylab.xhuschedule.repository.dataSource

import androidx.lifecycle.MediatorLiveData
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import vip.mystery0.rxpackagedata.PackageData

interface TestDataSource {
	fun queryAllTestsByUsername(testLiveData: MediatorLiveData<PackageData<List<Test>>>, student: Student)

	fun queryAllTestsForManyStudent(testLiveData: MediatorLiveData<PackageData<List<Test>>>, studentList: List<Student>)
}