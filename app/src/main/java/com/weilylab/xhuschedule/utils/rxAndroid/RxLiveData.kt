package com.weilylab.xhuschedule.utils.rxAndroid

import androidx.lifecycle.LiveData
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

class RxLiveData<T>(private val observable: Observable<T>) : LiveData<PackageData<T>>() {
	private val lock = Object()
	private var mDisposableRef: WeakReference<Disposable>? = null

	override fun onActive() {
		super.onActive()
		observable.subscribe(object : Observer<T> {
			override fun onSubscribe(d: Disposable) {
				synchronized(lock) {
					mDisposableRef = WeakReference(d)
				}
				postValue(PackageData.loading())
			}

			override fun onNext(t: T) {
				postValue(PackageData.content(t))
			}

			override fun onError(e: Throwable) {
				synchronized(lock) {
					mDisposableRef = null
				}
				postValue(PackageData.error(e))
			}

			override fun onComplete() {
				synchronized(lock) {
					mDisposableRef = null
				}
			}
		})
	}

	override fun onInactive() {
		super.onInactive()
		synchronized(lock) {
			val subscriptionRef = mDisposableRef
			if (subscriptionRef != null) {
				subscriptionRef.get()?.dispose()
				mDisposableRef = null
			}
		}
	}
}