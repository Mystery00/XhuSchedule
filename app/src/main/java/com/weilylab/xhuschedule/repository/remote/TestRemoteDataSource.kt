package com.weilylab.xhuschedule.repository.remote

import androidx.lifecycle.MediatorLiveData
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.listener.DoSaveListener
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.repository.dataSource.TestDataSource
import com.weilylab.xhuschedule.repository.local.TestLocalDataSource
import com.weilylab.xhuschedule.utils.NetworkUtil
import com.weilylab.xhuschedule.utils.TestUtil
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import vip.mystery0.logs.Logs

object TestRemoteDataSource : TestDataSource {
	override fun queryAllTestsByUsername(testLiveData: MediatorLiveData<PackageData<List<Test>>>, student: Student) {
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
					val testList = TestUtil.filterTestList(t)
					if (testList.isNotEmpty())
						testLiveData.value = PackageData.content(testList)
					else
						testLiveData.value = PackageData.empty()
				}

				override fun error(rt: String, msg: String?) {
					testLiveData.value = PackageData.error(Exception(msg))
					TestLocalDataSource.queryAllTestsByUsername(testLiveData, student)
				}
			})
		} else {
			testLiveData.value = PackageData.error(Exception(StringConstant.hint_network_error))
			TestLocalDataSource.queryAllTestsByUsername(testLiveData, student)
		}
	}

	override fun queryAllTestsForManyStudent(testLiveData: MediatorLiveData<PackageData<List<Test>>>, studentList: List<Student>) {
		if (NetworkUtil.isConnectInternet()) {
			TestUtil.getTestsForManyStudent(studentList, object : DoSaveListener<Map<String, List<Test>>> {
				override fun doSave(t: Map<String, List<Test>>) {
					val username = t.keys.first()
					val courseList = t[username]!!
					TestLocalDataSource.deleteAllTestsForStudent(username)
					courseList.forEach {
						it.studentID = username
					}
					TestLocalDataSource.saveTests(courseList)
				}
			}, object : RequestListener<List<Test>> {
				override fun done(t: List<Test>) {
					val testList = TestUtil.filterTestList(t)
					if (testList.isNotEmpty())
						testLiveData.value = PackageData.content(testList)
					else
						testLiveData.value = PackageData.empty()
				}

				override fun error(rt: String, msg: String?) {
					testLiveData.value = PackageData.error(Exception(msg))
					TestLocalDataSource.queryAllTestsForManyStudent(testLiveData, studentList)
				}
			})
		} else {
			testLiveData.value = PackageData.error(Exception(StringConstant.hint_network_error))
			TestLocalDataSource.queryAllTestsForManyStudent(testLiveData, studentList)
		}
	}
}