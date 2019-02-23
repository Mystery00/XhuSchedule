package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.CustomThing
import com.weilylab.xhuschedule.repository.local.service.impl.CustomThingServiceImpl
import com.weilylab.xhuschedule.utils.CalendarUtil
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.rx.RxObservable
import vip.mystery0.rxpackagedata.rx.RxObserver

object CustomThingLocalDataSource {
	private val customThingService by lazy { CustomThingServiceImpl() }

	fun getToday(customThingLiveData: MutableLiveData<PackageData<List<CustomThing>>>) {
		RxObservable<List<CustomThing>>()
				.doThings { observableEmitter ->
					val list = customThingService.queryAllThings()
					observableEmitter.onFinish(list.filter { CalendarUtil.isThingToday(it) })
				}
				.subscribe(object : RxObserver<List<CustomThing>>() {
					override fun onError(e: Throwable) {
						customThingLiveData.value = PackageData.error(e)
					}

					override fun onFinish(data: List<CustomThing>?) {
						when {
							data == null -> customThingLiveData.value = PackageData.error(Exception("data is null"))
							data.isEmpty() -> customThingLiveData.value = PackageData.empty(data)
							else -> customThingLiveData.value = PackageData.content(data)
						}
					}
				})
	}

	/**
	 * 该接口提供给事项管理页面使用
	 */
	fun getAll(customThingLiveData: MutableLiveData<PackageData<List<CustomThing>>>) {
		RxObservable<List<CustomThing>>()
				.doThings { observableEmitter ->
					observableEmitter.onFinish(customThingService.queryAllThings())
				}
				.subscribe(object : RxObserver<List<CustomThing>>() {
					override fun onError(e: Throwable) {
						customThingLiveData.value = PackageData.error(e)
					}

					override fun onFinish(data: List<CustomThing>?) {
						when {
							data == null -> customThingLiveData.value = PackageData.error(Exception("data is null"))
							data.isEmpty() -> customThingLiveData.value = PackageData.empty(data)
							else -> customThingLiveData.value = PackageData.content(data)
						}
					}
				})
	}

	fun save(thing: CustomThing, listener: (Boolean, Throwable?) -> Unit) {
		RxObservable<Boolean>()
				.doThings {
					customThingService.addThing(thing)
					it.onFinish(true)
				}
				.subscribe(object : RxObserver<Boolean>() {
					override fun onError(e: Throwable) {
						listener.invoke(false, e)
					}

					override fun onFinish(data: Boolean?) {
						listener.invoke(data != null && data, null)
					}
				})
	}

	fun update(thing: CustomThing, listener: (Boolean, Throwable?) -> Unit) {
		RxObservable<Boolean>()
				.doThings {
					customThingService.updateThing(thing)
					it.onFinish(true)
				}
				.subscribe(object : RxObserver<Boolean>() {
					override fun onError(e: Throwable) {
						listener.invoke(false, e)
					}

					override fun onFinish(data: Boolean?) {
						listener.invoke(data != null && data, null)
					}
				})
	}

	fun delete(thing: CustomThing, listener: (Boolean) -> Unit) {
		RxObservable<Boolean>()
				.doThings {
					customThingService.deleteThing(thing)
					it.onFinish(true)
				}
				.subscribe(object : RxObserver<Boolean>() {
					override fun onError(e: Throwable) {
						listener.invoke(false)
					}

					override fun onFinish(data: Boolean?) {
						listener.invoke(data != null && data)
					}
				})
	}
}