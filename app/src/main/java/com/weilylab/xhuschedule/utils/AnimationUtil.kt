/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.utils

import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object AnimationUtil {
	@SuppressLint("CheckResult")
	fun setWindowAlpha(context: Context?, startAlpha: Float, endAlpha: Float, duration: Long, time: Int = 10) {
		val layoutParams = (context as Activity).window.attributes
		val step: Float = (endAlpha - startAlpha) / time.toFloat()
		val interval: Long = duration / time
		Observable.create<Float> {
			for ((index, _) in (0 until duration step interval).withIndex()) {
				val alpha = startAlpha + index * step
				it.onNext(alpha)
				Thread.sleep(interval)
			}
			it.onComplete()
		}
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe {
					val alpha = if (it !in 0F..1F) if (it > 1F) 1F else 0F else it
					layoutParams.alpha = alpha
					context.window.attributes = layoutParams
				}
	}

	private var animator: ValueAnimator? = null

	fun expandLayout(target: View, start: Int, end: Int) {
		if (target.layoutParams.height == end)
			return
		animator?.cancel()
		animator = ValueAnimator.ofInt(start, end)
		val evaluator = IntEvaluator()
		animator?.addUpdateListener {
			target.layoutParams.height = evaluator.evaluate(it.animatedFraction, start, end)
			target.requestLayout()
		}
		animator?.duration = 200
		animator?.start()
	}
}