package com.weilylab.xhuschedule.newPackage.repository.local

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.Test
import com.weilylab.xhuschedule.newPackage.repository.dataSource.TestDataSource
import com.weilylab.xhuschedule.newPackage.repository.local.service.impl.TestServiceImpl
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObserver

object TestLocalDataSource : TestDataSource {
	private val testService = TestServiceImpl()

	override fun queryAllTests(testLiveData: MediatorLiveData<PackageData<List<Test>>>, student: Student) {
		testLiveData.value=PackageData.loading()
		RxObservable<List<Test>>()
				.doThings {
					try {
						it.onFinish(testService.queryAllTest())
					} catch (e: Exception) {
						it.onError(e)
					}
				}
				.subscribe(object : RxObserver<List<Test>>() {
					override fun onFinish(data: List<Test>?) {
						testLiveData.value= PackageData.content(data)
					}

					override fun onError(e: Throwable) {
						testLiveData.value= PackageData.error(e)
					}
				})
	}

	fun queryTestsOnThisDay(testLiveData: MutableLiveData<Test>, messageLiveData: MutableLiveData<String>, requestCodeLiveData: MutableLiveData<Int>, day: String, student: Student) {
	}

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