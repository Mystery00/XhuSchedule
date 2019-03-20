package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.repository.ds.TestDataSource
import com.weilylab.xhuschedule.repository.local.service.TestService
import com.weilylab.xhuschedule.repository.local.service.impl.TestServiceImpl
import com.weilylab.xhuschedule.utils.userDo.TestUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs
import vip.mystery0.rx.OnlyCompleteObserver
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.StartAndCompleteObserver
import java.util.ArrayList

object TestLocalDataSource : TestDataSource {
	private val testService: TestService by lazy { TestServiceImpl() }

	override fun queryAllTestsByUsername(testLiveData: MediatorLiveData<PackageData<List<Test>>>, htmlLiveData: MutableLiveData<String>?, student: Student) {
		Observable.create<List<Test>> {
			it.onNext(testService.queryTestsForStudent(student.username))
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : StartAndCompleteObserver<List<Test>>() {
					override fun onSubscribe(d: Disposable) {
						testLiveData.value = PackageData.loading()
					}

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
		Observable.create<List<Test>> {
			val tests = ArrayList<Test>()
			studentList.forEach { s ->
				tests.addAll(testService.queryTestsForStudent(s.username))
			}
			it.onNext(tests)
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<List<Test>>() {
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