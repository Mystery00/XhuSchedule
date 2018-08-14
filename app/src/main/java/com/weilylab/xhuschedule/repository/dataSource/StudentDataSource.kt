package com.weilylab.xhuschedule.repository.dataSource

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData

interface StudentDataSource {
	fun queryStudentInfo(studentInfoLiveData: MutableLiveData<PackageData<StudentInfo>>, student: Student)
}