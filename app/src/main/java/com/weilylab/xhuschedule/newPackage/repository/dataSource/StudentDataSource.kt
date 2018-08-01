package com.weilylab.xhuschedule.newPackage.repository.dataSource

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.StudentInfo
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData

interface StudentDataSource {
	fun queryStudentInfo(studentInfoLiveData: MutableLiveData<PackageData<StudentInfo>>, student: Student)
}