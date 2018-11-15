package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MediatorLiveData
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.repository.dataSource.TestDataSource
import com.weilylab.xhuschedule.repository.local.service.TestService
import com.weilylab.xhuschedule.repository.local.service.impl.TestServiceImpl
import com.weilylab.xhuschedule.utils.TestUtil
import vip.mystery0.logs.Logs
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.rx.RxObservable
import vip.mystery0.rxpackagedata.rx.RxObserver
import java.util.ArrayList

object TestLocalDataSource : TestDataSource {
	private val testService: TestService by lazy { TestServiceImpl() }

	override fun queryAllTestsByUsername(testLiveData: MediatorLiveData<PackageData<List<Test>>>, student: Student) {
		testLiveData.value = PackageData.loading()
		RxObservable<List<Test>>()
				.doThings {
					it.onFinish(testService.queryTestsForStudent(student.username))
				}
				.subscribe(object : RxObserver<List<Test>>() {
					override fun onFinish(data: List<Test>?) {
						if (data == null)
							testLiveData.value = PackageData.empty()
						else {
							val testList = TestUtil.filterTestList(data)
							if (testList.isNotEmpty())
								testLiveData.value = PackageData.content(testList)
							else
								testLiveData.value = PackageData.empty()
						}
					}

					override fun onError(e: Throwable) {
						testLiveData.value = PackageData.error(e)
					}
				})
	}

	override fun queryAllTestsForManyStudent(testLiveData: MediatorLiveData<PackageData<List<Test>>>, studentList: List<Student>) {
		RxObservable<List<Test>>()
				.doThings { emitter ->
					val tests = ArrayList<Test>()
					studentList.forEach {
						tests.addAll(testService.queryTestsForStudent(it.username))
					}
					emitter.onFinish(tests)
				}
				.subscribe(object : RxObserver<List<Test>>() {
					override fun onFinish(data: List<Test>?) {
						if (data == null)
							testLiveData.value = PackageData.empty()
						else {
							val testList = TestUtil.filterTestList(data)
							if (testList.isNotEmpty())
								testLiveData.value = PackageData.content(testList)
							else
								testLiveData.value = PackageData.empty()
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						testLiveData.value = PackageData.error(e)
					}
				})
	}

	fun getRawTestList(student: Student): List<Test> = testService.queryTestsForStudent(student.username)

	fun deleteAllTestsForStudent(username: String) {
		val list = testService.queryTestsForStudent(username)
		list.forEach {
			testService.delete(it)
		}
	}

	fun saveTests(tests: List<Test>) {
		tests.forEach {
			testService.insert(it)
		}
	}
}