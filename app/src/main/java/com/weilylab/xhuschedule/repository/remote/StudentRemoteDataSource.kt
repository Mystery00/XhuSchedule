package com.weilylab.xhuschedule.repository.remote

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.listener.DoSaveListener
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.repository.ds.StudentDataSource
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import vip.mystery0.tools.utils.NetworkTools
import com.weilylab.xhuschedule.utils.userDo.UserUtil
import vip.mystery0.rx.PackageData

object StudentRemoteDataSource : StudentDataSource {
	override fun queryStudentInfo(studentInfoLiveData: MutableLiveData<PackageData<StudentInfo>>, student: Student) {
		studentInfoLiveData.value = PackageData.loading()
		if (NetworkTools.instance.isConnectInternet()) {
			queryStudentInfo({ studentInfo, exception ->
				if (exception == null)
					studentInfoLiveData.value = PackageData.content(studentInfo)
				else
					studentInfoLiveData.value = PackageData.error(exception)
			}, student)
		} else {
			studentInfoLiveData.value = PackageData.error(Exception(StringConstant.hint_network_error))
		}
	}

	fun queryStudentInfo(listener: (StudentInfo?, Exception?) -> Unit, student: Student) {
		UserUtil.getInfo(student, object : DoSaveListener<StudentInfo> {
			override fun doSave(t: StudentInfo) {
				t.studentID = student.username
				StudentLocalDataSource.saveStudentInfo(t)
			}
		}, object : RequestListener<StudentInfo> {
			override fun done(t: StudentInfo) {
				listener.invoke(t, null)
			}

			override fun error(rt: String, msg: String?) {
				listener.invoke(null, Exception(msg))
			}
		})
	}

	fun login(loginLiveData: MutableLiveData<PackageData<Student>>, student: Student) {
		loginLiveData.value = PackageData.loading()
		UserUtil.checkStudentLogged(student) {
			if (!it) {
				if (NetworkTools.instance.isConnectInternet()) {
					UserUtil.login(student, object : DoSaveListener<Student> {
						override fun doSave(t: Student) {
							StudentLocalDataSource.saveStudent(student)
						}
					}, object : RequestListener<Boolean> {
						override fun done(t: Boolean) {
							loginLiveData.value = PackageData.content(student)
						}

						override fun error(rt: String, msg: String?) {
							loginLiveData.value = PackageData.error(Exception(msg))
						}
					})
				} else {
					loginLiveData.value = PackageData.error(Exception(StringConstant.hint_network_error))
				}
			} else {
				loginLiveData.value = PackageData.error(Exception(StringConstant.hint_student_logged))
			}
		}
	}
}