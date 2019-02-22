package com.weilylab.xhuschedule.repository.ds

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import vip.mystery0.rxpackagedata.PackageData

interface StudentDataSource {
	fun queryStudentInfo(studentInfoLiveData: MutableLiveData<PackageData<StudentInfo>>, student: Student)
}