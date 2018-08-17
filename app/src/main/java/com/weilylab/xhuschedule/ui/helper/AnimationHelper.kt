package com.weilylab.xhuschedule.ui.helper

import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs

object AnimationHelper {
	private val interpolator = AccelerateDecelerateInterpolator()
	private val nowPlayingList = ArrayList<View>()

	fun translationY(target: View, start: Int, end: Int, duration: Long) {
		Logs.im("translationY: ", start, end)
		val layoutParams = target.layoutParams as ViewGroup.MarginLayoutParams
		Observable.create<Int> {
			var index = 0F
			var frameNum: Int = (duration / 10).toInt()
			if (frameNum <= 0) frameNum = 1
			val pieceTime: Float = 1F / frameNum
			val sleepTime: Long = (duration * pieceTime).toLong()
			val length = end - start
			while (index <= 1) {
				it.onNext((interpolator.getInterpolation(index) * length).toInt())
				Thread.sleep(sleepTime)
				index += pieceTime
			}
			it.onComplete()
		}.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : Observer<Int> {
					override fun onComplete() {
						layoutParams.setMargins(0, end, 0, 0)
						target.layoutParams = layoutParams
						nowPlayingList.remove(target)
					}

					override fun onSubscribe(d: Disposable) {
						nowPlayingList.add(target)
					}

					override fun onNext(t: Int) {
						val top = start + t
						layoutParams.setMargins(0, top, 0, 0)
						target.layoutParams = layoutParams
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
					}
				})
	}

	fun move(target: View, start: Int, current: Int) {
		val layoutParams = target.layoutParams as ViewGroup.MarginLayoutParams
		val top = start + current
		layoutParams.setMargins(0, top, 0, 0)
		target.layoutParams = layoutParams
	}
}