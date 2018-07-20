package com.weilylab.xhuschedule.newPackage.repository.remote

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.constant.StringConstant
import com.weilylab.xhuschedule.newPackage.listener.DoSaveListener
import com.weilylab.xhuschedule.newPackage.listener.RequestListener
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.StudentInfo
import com.weilylab.xhuschedule.newPackage.repository.BottomNavigationRepository
import com.weilylab.xhuschedule.newPackage.repository.LoginRepository
import com.weilylab.xhuschedule.newPackage.repository.dataSource.StudentDataSource
import com.weilylab.xhuschedule.newPackage.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.newPackage.utils.NetworkUtil
import com.weilylab.xhuschedule.newPackage.utils.UserUtil
import vip.mystery0.logs.Logs

object StudentRemoteDataSource : StudentDataSource {
	override fun queryStudentInfo(studentInfoLiveData: MutableLiveData<StudentInfo>, messageLiveData: MutableLiveData<String>, requestCodeLiveData: MutableLiveData<Int>, student: Student) {
		if (NetworkUtil.isConnectInternet()) {
			UserUtil.getInfo(student, object : DoSaveListener<StudentInfo> {
				override fun doSave(t: StudentInfo) {
					t.studentID = student.username
					StudentLocalDataSource.saveStudentInfo(t)
				}

			}, object : RequestListener<StudentInfo> {
				override fun done(t: StudentInfo) {
					studentInfoLiveData.value = t
					requestCodeLiveData.value = BottomNavigationRepository.DONE
				}

				override fun error(rt: String, msg: String?) {
					Logs.im(rt, msg)
					StudentLocalDataSource.queryStudentInfo(studentInfoLiveData, messageLiveData, requestCodeLiveData, student)
				}
			})
		} else {
			messageLiveData.value = StringConstant.hint_network_error
			StudentLocalDataSource.queryStudentInfo(studentInfoLiveData, messageLiveData, requestCodeLiveData, student)
		}
	}

	fun login(messageLiveData: MutableLiveData<String>, requestCodeLiveData: MutableLiveData<Int>, student: Student) {
		if (NetworkUtil.isConnectInternet()) {
			UserUtil.login(student, object : DoSaveListener<Student> {
				override fun doSave(t: Student) {
					StudentLocalDataSource.saveStudent(student)
				}
			}, object : RequestListener<Boolean> {
				override fun done(t: Boolean) {
					requestCodeLiveData.value = LoginRepository.DONE
				}

				override fun error(rt: String, msg: String?) {
					requestCodeLiveData.value = LoginRepository.ERROR
					messageLiveData.value = msg
				}
			})
		} else {
			requestCodeLiveData.value = LoginRepository.ERROR
			messageLiveData.value = StringConstant.hint_network_error
		}
	}
}