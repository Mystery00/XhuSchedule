package com.weilylab.xhuschedule.newPackage.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.Test
import com.weilylab.xhuschedule.newPackage.repository.TestRepository
import com.weilylab.xhuschedule.newPackage.repository.dataSource.TestDataSource
import com.weilylab.xhuschedule.newPackage.repository.local.service.impl.TestServiceImpl
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObserver
import vip.mystery0.logs.Logs

object TestLocalDataSource : TestDataSource {
	private val testService = TestServiceImpl()

	override fun queryAllTests(testLiveData: MutableLiveData<List<Test>>, messageLiveData: MutableLiveData<String>, requestCodeLiveData: MutableLiveData<Int>, student: Student) {
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
						requestCodeLiveData.value = TestRepository.DONE
						testLiveData.value = data
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						messageLiveData.value = e.message
						requestCodeLiveData.value = TestRepository.ERROR
					}
				})
	}

	fun queryTestsOnThisDay(testLiveData: MutableLiveData<Test>, messageLiveData: MutableLiveData<String>, requestCodeLiveData: MutableLiveData<Int>, day: String, student: Student) {
	}

	fun saveTests(tests: List<Test>) {

	}
}