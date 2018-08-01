package com.weilylab.xhuschedule.newPackage.repository.remote

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.constant.StringConstant
import com.weilylab.xhuschedule.newPackage.listener.DoSaveListener
import com.weilylab.xhuschedule.newPackage.listener.RequestListener
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.StudentInfo
import com.weilylab.xhuschedule.newPackage.repository.dataSource.StudentDataSource
import com.weilylab.xhuschedule.newPackage.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.newPackage.utils.NetworkUtil
import com.weilylab.xhuschedule.newPackage.utils.UserUtil
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData

object StudentRemoteDataSource : StudentDataSource {
	override fun queryStudentInfo(studentInfoLiveData: MutableLiveData<PackageData<StudentInfo>>, student: Student) {
		studentInfoLiveData.value = PackageData.loading()
		if (NetworkUtil.isConnectInternet()) {
			UserUtil.getInfo(student, object : DoSaveListener<StudentInfo> {
				override fun doSave(t: StudentInfo) {
					t.studentID = student.username
					StudentLocalDataSource.saveStudentInfo(t)
				}

			}, object : RequestListener<StudentInfo> {
				override fun done(t: StudentInfo) {
					studentInfoLiveData.value = PackageData.content(t)
				}

				override fun error(rt: String, msg: String?) {
					studentInfoLiveData.value = PackageData.error(Exception(msg))
					StudentLocalDataSource.queryStudentInfo(studentInfoLiveData, student)
				}
			})
		} else {
			studentInfoLiveData.value = PackageData.error(Exception(StringConstant.hint_network_error))
			StudentLocalDataSource.queryStudentInfo(studentInfoLiveData, student)
		}
	}

	fun login(loginLiveData: MutableLiveData<PackageData<Boolean>>, student: Student) {
		loginLiveData.value = PackageData.loading()
		if (NetworkUtil.isConnectInternet()) {
			UserUtil.login(student, object : DoSaveListener<Student> {
				override fun doSave(t: Student) {
					StudentLocalDataSource.saveStudent(student)
				}
			}, object : RequestListener<Boolean> {
				override fun done(t: Boolean) {
					loginLiveData.value = PackageData.content(true)
				}

				override fun error(rt: String, msg: String?) {
					loginLiveData.value = PackageData.error(Exception(msg))
				}
			})
		} else {
			loginLiveData.value = PackageData.error(Exception(StringConstant.hint_network_error))
		}
	}
}