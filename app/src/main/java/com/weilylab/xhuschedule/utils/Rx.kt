package com.weilylab.xhuschedule.utils

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs

class RxObservable<T> {
	fun doThings(listener: (RxObservableEmitter<T>) -> Unit): Observable<T> = Observable.create<T> {
		val emitter = object : RxObservableEmitter<T> {
			override fun onError(error: Throwable) {
				it.onError(error)
			}

			override fun onFinish(data: T) {
				it.onNext(data)
				it.onComplete()
			}
		}
		listener(emitter)
	}
			.subscribeOn(Schedulers.newThread())
			.unsubscribeOn(Schedulers.newThread())
			.observeOn(AndroidSchedulers.mainThread())

	fun doThingsOnThread(listener: (RxObservableEmitter<T>) -> Unit): Observable<T> = Observable.create<T> {
		val emitter = object : RxObservableEmitter<T> {
			override fun onError(error: Throwable) {
				it.onError(error)
			}

			override fun onFinish(data: T) {
				it.onNext(data)
				it.onComplete()
			}
		}
		listener(emitter)
	}
			.subscribeOn(Schedulers.newThread())
			.unsubscribeOn(Schedulers.newThread())
			.observeOn(Schedulers.newThread())

	interface RxObservableEmitter<T> {
		fun onError(error: Throwable)

		fun onFinish(data: T)
	}
}

abstract class RxObserver<T> : Observer<T> {
	private var data: T? = null
	override fun onSubscribe(d: Disposable) {
	}

	override fun onNext(t: T) {
		data = t
	}

	override fun onComplete() {
		onFinish(data)
	}

	abstract fun onFinish(data: T?)
}

class DoNothingObserver<T>(private val isLog: Boolean = false) : RxObserver<T>() {

	override fun onFinish(data: T?) {
		if (isLog)
			Logs.i("onFinish: $data")
	}

	override fun onError(e: Throwable) {
		if (isLog)
			Logs.e("onError: ", e)
	}
}