package com.weilylab.xhuschedule.repository.remote

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.listener.DoSaveListener
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.CustomThing
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.SyncCustomThing
import com.weilylab.xhuschedule.model.response.GetUserDataResponse
import com.weilylab.xhuschedule.repository.local.CustomThingLocalDataSource
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.utils.userDo.UserUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.StartAndCompleteObserver
import vip.mystery0.tools.factory.fromJson
import vip.mystery0.tools.factory.toJson
import vip.mystery0.tools.utils.NetworkTools

object CustomThingRemoteDataSource {
	fun syncCustomThingForLocal(statusLiveData: MutableLiveData<PackageData<Boolean>>, key: String) {
		if (NetworkTools.instance.isConnectInternet()) {
			Observable.create<Student> {
				val main = StudentLocalDataSource.queryMainStudent()
				if (main != null)
					it.onNext(main)
				it.onComplete()
			}
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : StartAndCompleteObserver<Student>() {
						override fun onSubscribe(d: Disposable) {
							statusLiveData.value = PackageData.loading()
						}

						override fun onError(e: Throwable) {
							statusLiveData.value = PackageData.error(e)
						}

						override fun onFinish(data: Student?) {
							if (data == null) {
								statusLiveData.value = PackageData.error(Exception(StringConstant.hint_null_student))
							} else {
								UserUtil.getUserData(data, key, object : DoSaveListener<GetUserDataResponse> {
									override fun doSave(t: GetUserDataResponse) {
										val sync = t.value.fromJson<SyncCustomThing>()
										CustomThingLocalDataSource.syncLocal(sync.list)
									}
								}, object : RequestListener<String> {
									override fun done(t: String) {
										statusLiveData.value = PackageData.content(true)
									}

									override fun error(rt: String, msg: String?) {
										statusLiveData.value = PackageData.error(Exception(msg))
									}
								})
							}
						}
					})
		} else {
			statusLiveData.value = PackageData.error(Exception(StringConstant.hint_network_error))
		}
	}

	fun syncCustomThingForServer(statusLiveData: MutableLiveData<PackageData<Boolean>>, key: String) {
		if (NetworkTools.instance.isConnectInternet()) {
			Observable.create<List<CustomThing>> {
				it.onNext(CustomThingLocalDataSource.getRawCustomThingList())
				it.onComplete()
			}
					.subscribeOn(Schedulers.io())
					.map {
						val main = StudentLocalDataSource.queryMainStudent()
						Pair(main, it)
					}
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : StartAndCompleteObserver<Pair<Student?, List<CustomThing>>>() {
						override fun onError(e: Throwable) {
							statusLiveData.value = PackageData.error(e)
						}

						override fun onFinish(data: Pair<Student?, List<CustomThing>>?) {
							if (data?.first == null) {
								statusLiveData.value = PackageData.content(false)
								return
							}
							UserUtil.setUserData(data.first!!, key, SyncCustomThing(data.second).toJson(), object : RequestListener<Boolean> {
								override fun done(t: Boolean) {
									statusLiveData.value = PackageData.content(false)
								}

								override fun error(rt: String, msg: String?) {
									statusLiveData.value = PackageData.error(Exception(msg))
								}
							})
						}

						override fun onSubscribe(d: Disposable) {
							statusLiveData.value = PackageData.loading()
						}
					})
		} else {
			statusLiveData.value = PackageData.error(Exception(StringConstant.hint_network_error))
		}
	}
}