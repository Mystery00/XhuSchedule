package com.weilylab.xhuschedule.utils

import com.weilylab.xhuschedule.api.TestAPI
import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.factory.GsonFactory
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.listener.DoSaveListener
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.response.TestResponse
import vip.mystery0.rxpackagedata.rx.RxObservable
import vip.mystery0.rxpackagedata.rx.RxObserver
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs
import java.util.ArrayList
import java.util.HashMap

object TestUtil {
	private const val RETRY_TIME = 1

	fun getTests(student: Student, doSaveListener: DoSaveListener<List<Test>>?, requestListener: RequestListener<List<Test>>, index: Int = 0) {
		RetrofitFactory.retrofit
				.create(TestAPI::class.java)
				.getTests(student.username)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map {
					val testResponse = GsonFactory.parse<TestResponse>(it)
					if (testResponse.rt == ResponseCodeConstants.DONE)
						doSaveListener?.doSave(testResponse.tests)
					testResponse
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : RxObserver<TestResponse>() {
					override fun onFinish(data: TestResponse?) {
						when {
							data == null -> requestListener.error(ResponseCodeConstants.UNKNOWN_ERROR, StringConstant.hint_data_null)
							data.rt == ResponseCodeConstants.DONE -> requestListener.done(data.tests)
							data.rt == ResponseCodeConstants.ERROR_NOT_LOGIN -> {
								if (index == RETRY_TIME)
									requestListener.error(ResponseCodeConstants.DO_TOO_MANY, StringConstant.hint_do_too_many)
								else
									UserUtil.login(student, null, object : RequestListener<Boolean> {
										override fun done(t: Boolean) {
											getTests(student, doSaveListener, requestListener, index + 1)
										}

										override fun error(rt: String, msg: String?) {
											requestListener.error(rt, msg)
										}
									})
							}
							else -> requestListener.error(data.rt, data.msg)
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						requestListener.error(ResponseCodeConstants.CATCH_ERROR, e.message)
					}
				})
	}

	fun getTestsForManyStudent(studentList: List<Student>, doSaveListener: DoSaveListener<Map<String, List<Test>>>?, requestListener: RequestListener<List<Test>>) {
		try {
			val maxIndex = 2
			val resultArray = BooleanArray(studentList.size) { false }
			val map = HashMap<String, List<Test>>()
			request(resultArray, studentList, doSaveListener, map, {
				val testList = ArrayList<Test>()
				map.keys.forEach {
					testList.addAll(map[it]!!)
				}
				requestListener.done(testList)
			}, maxIndex)
		} catch (e: Exception) {
			requestListener.error(ResponseCodeConstants.CATCH_ERROR, e.message)
		}
	}

	private fun request(resultArray: BooleanArray, studentList: List<Student>, doSaveListener: DoSaveListener<Map<String, List<Test>>>?, map: HashMap<String, List<Test>>, doneListener: () -> Unit, maxIndex: Int, index: Int = 0) {
		if (index >= maxIndex || isAllDone(resultArray)) {
			doneListener.invoke()
			return
		}
		val needRequestArray = ArrayList<Observable<DataWithUsername<TestResponse>>>()
		resultArray.filter { !it }
				.forEachIndexed { position, _ ->
					needRequestArray.add(RetrofitFactory.retrofit
							.create(TestAPI::class.java)
							.getTests(studentList[position].username)
							.subscribeOn(Schedulers.newThread())
							.unsubscribeOn(Schedulers.newThread())
							.map {
								val testResponse = GsonFactory.parse<TestResponse>(it)
								if (testResponse.rt == ResponseCodeConstants.DONE) {
									val saveMap = HashMap<String, List<Test>>()
									saveMap[studentList[position].username] = testResponse.tests
									doSaveListener?.doSave(saveMap)
								}
								TestUtil.DataWithUsername(studentList[position].username, testResponse)
							})
				}
		Observable.merge(needRequestArray)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : Observer<DataWithUsername<TestResponse>> {
					override fun onComplete() {
						resumeRequest(resultArray, studentList, doSaveListener, map, doneListener, maxIndex, index)
					}

					override fun onSubscribe(d: Disposable) {
					}

					override fun onNext(t: DataWithUsername<TestResponse>) {
						val username = t.username
						val data = t.data!!
						val position = studentList.indexOfFirst { it.username == username }
						when {
							data.rt == ResponseCodeConstants.DONE -> {
								map[username] = data.tests
								resultArray[position] = true
							}
							data.rt == ResponseCodeConstants.ERROR_NOT_LOGIN -> {
								if (index == TestUtil.RETRY_TIME)
									resultArray[position] = false
								else
									UserUtil.login(studentList[position], null, object : RequestListener<Boolean> {
										override fun done(t: Boolean) {
											resultArray[position] = false
										}

										override fun error(rt: String, msg: String?) {
											Logs.em("error: ", rt, msg)
											resultArray[position] = false
										}
									})
							}
							else -> resultArray[position] = false
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						resumeRequest(resultArray, studentList, doSaveListener, map, doneListener, maxIndex, index)
					}
				})
	}

	private fun resumeRequest(resultArray: BooleanArray, studentList: List<Student>, doSaveListener: DoSaveListener<Map<String, List<Test>>>?, map: HashMap<String, List<Test>>, doneListener: () -> Unit, maxIndex: Int, index: Int) {
		RxObservable<Boolean>()
				.doThings {
					Thread.sleep(200)
					it.onFinish(true)
				}
				.subscribe(object : RxObserver<Boolean>() {
					override fun onFinish(data: Boolean?) {
						request(resultArray, studentList, doSaveListener, map, doneListener, maxIndex, index + 1)
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
					}
				})
	}

	private fun isAllDone(resultArray: BooleanArray): Boolean {
		resultArray.forEach {
			if (!it)
				return false
		}
		return true
	}

	fun filterTestList(testList: List<Test>): List<Test> = testList.filter { it.date != "" || it.testno != "" || it.time != "" || it.location != "" }

	internal class DataWithUsername<T>(val username: String, val data: T?)
}