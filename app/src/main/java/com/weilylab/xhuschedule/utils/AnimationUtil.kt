package com.weilylab.xhuschedule.utils

import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
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

	fun expandLayout(target: View, collapsedHeight: Int, expandedHeight: Int, isExpand: Boolean) {
		val start = if (isExpand) collapsedHeight else expandedHeight
		val end = if (isExpand) expandedHeight else collapsedHeight
		if (target.layoutParams.height == end)
			return
		val valueAnimator = ValueAnimator.ofInt(start, end)
		val evaluator = IntEvaluator()
		valueAnimator.addUpdateListener {
			target.layoutParams.height = evaluator.evaluate(it.animatedFraction, start, end)
			target.requestLayout()
		}
		valueAnimator.setDuration(200).start()
	}
}