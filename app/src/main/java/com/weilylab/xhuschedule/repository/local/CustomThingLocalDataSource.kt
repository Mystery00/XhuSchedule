package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.CustomThing
import com.weilylab.xhuschedule.repository.local.service.impl.CustomThingServiceImpl
import com.weilylab.xhuschedule.utils.CalendarUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.rx.OnlyCompleteObserver
import vip.mystery0.rx.PackageData

object CustomThingLocalDataSource {
	private val customThingService by lazy { CustomThingServiceImpl() }

	fun getToday(customThingLiveData: MutableLiveData<PackageData<List<CustomThing>>>) {
		Observable.create<List<CustomThing>> {
			it.onNext(customThingService.queryAllThings())
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.computation())
				.map { it.filter { c -> CalendarUtil.isThingOnDay(c) } }
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<List<CustomThing>>() {
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
		Observable.create<List<CustomThing>> {
			it.onNext(customThingService.queryAllThings())
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<List<CustomThing>>() {
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

	fun getRawCustomThingList(): List<CustomThing> = customThingService.queryAllThings()

	fun save(thing: CustomThing, listener: (Boolean, Throwable?) -> Unit) {
		Observable.create<Boolean> {
			customThingService.addThing(thing)
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<Boolean>() {
					override fun onError(e: Throwable) {
						listener.invoke(false, e)
					}

					override fun onFinish(data: Boolean?) {
						listener.invoke(data != null && data, null)
					}
				})
	}

	fun update(thing: CustomThing, listener: (Boolean, Throwable?) -> Unit) {
		Observable.create<Boolean> {
			customThingService.updateThing(thing)
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<Boolean>() {
					override fun onError(e: Throwable) {
						listener.invoke(false, e)
					}

					override fun onFinish(data: Boolean?) {
						listener.invoke(data != null && data, null)
					}
				})
	}

	fun delete(thing: CustomThing, listener: (Boolean) -> Unit) {
		Observable.create<Boolean> {
			customThingService.deleteThing(thing)
			it.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<Boolean>() {
					override fun onError(e: Throwable) {
						listener.invoke(false)
					}

					override fun onFinish(data: Boolean?) {
						listener.invoke(data != null && data)
					}
				})
	}
}