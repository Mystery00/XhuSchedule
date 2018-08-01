package com.weilylab.xhuschedule.newPackage.repository.remote

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.constant.StringConstant
import com.weilylab.xhuschedule.newPackage.listener.DoSaveListener
import com.weilylab.xhuschedule.newPackage.listener.RequestListener
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.Test
import com.weilylab.xhuschedule.newPackage.repository.TestRepository
import com.weilylab.xhuschedule.newPackage.repository.dataSource.TestDataSource
import com.weilylab.xhuschedule.newPackage.repository.local.TestLocalDataSource
import com.weilylab.xhuschedule.newPackage.utils.NetworkUtil
import com.weilylab.xhuschedule.newPackage.utils.TestUtil
import vip.mystery0.logs.Logs

object TestRemoteDataSource : TestDataSource {
	override fun queryAllTests(testLiveData: MutableLiveData<List<Test>>, messageLiveData: MutableLiveData<String>, requestCodeLiveData: MutableLiveData<Int>, student: Student) {
		if (NetworkUtil.isConnectInternet()) {
			TestUtil.getTests(student, object : DoSaveListener<List<Test>> {
				override fun doSave(t: List<Test>) {
					TestLocalDataSource.deleteAllTestsForStudent(student.username)
					t.forEach {
						it.studentID = student.username
					}
					TestLocalDataSource.saveTests(t)
				}
			}, object : RequestListener<List<Test>> {
				override fun done(t: List<Test>) {
					requestCodeLiveData.value = TestRepository.DONE
					testLiveData.value = t
				}

				override fun error(rt: String, msg: String?) {
					Logs.im(rt, msg)
					TestLocalDataSource.queryAllTests(testLiveData, messageLiveData, requestCodeLiveData, student)
				}
			})
		} else {
			messageLiveData.value = StringConstant.hint_network_error
			TestLocalDataSource.queryAllTests(testLiveData, messageLiveData, requestCodeLiveData, student)
		}
	}
}