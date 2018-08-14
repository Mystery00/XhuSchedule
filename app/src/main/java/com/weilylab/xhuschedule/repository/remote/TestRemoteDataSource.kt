package com.weilylab.xhuschedule.repository.remote

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.listener.DoSaveListener
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.repository.TestRepository
import com.weilylab.xhuschedule.repository.dataSource.TestDataSource
import com.weilylab.xhuschedule.repository.local.TestLocalDataSource
import com.weilylab.xhuschedule.utils.NetworkUtil
import com.weilylab.xhuschedule.utils.TestUtil
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import vip.mystery0.logs.Logs

object TestRemoteDataSource : TestDataSource {
	override fun queryAllTests(testLiveData: MediatorLiveData<PackageData<List<Test>>>, student: Student) {
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
					testLiveData.value = PackageData.content(t)
				}

				override fun error(rt: String, msg: String?) {
					testLiveData.value = PackageData.error(Exception(msg))
					TestLocalDataSource.queryAllTests(testLiveData, student)
				}
			})
		} else {
			testLiveData.value = PackageData.error(Exception(StringConstant.hint_network_error))
			TestLocalDataSource.queryAllTests(testLiveData, student)
		}
	}
}