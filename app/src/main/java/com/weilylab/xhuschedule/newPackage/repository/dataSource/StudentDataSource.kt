package com.weilylab.xhuschedule.newPackage.repository.dataSource

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.StudentInfo

interface StudentDataSource {

	fun queryStudentInfo(studentInfoLiveData: MutableLiveData<StudentInfo>, messageLiveData: MutableLiveData<String>, requestCodeLiveData: MutableLiveData<Int>, student: Student)
}