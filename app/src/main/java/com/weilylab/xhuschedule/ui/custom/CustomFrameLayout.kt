package com.weilylab.xhuschedule.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import com.weilylab.xhuschedule.ui.helper.ScoreAnimationHelper

class CustomFrameLayout : FrameLayout {
	constructor(context: Context) : super(context)
	constructor(context: Context, attributes: AttributeSet?) : super(context, attributes)
	constructor(context: Context, attributes: AttributeSet?, defStyle: Int) : super(context, attributes, defStyle)

	lateinit var doOnTouchListener: (MotionEvent?) -> Unit

	override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
		when (ev?.action) {
			MotionEvent.ACTION_DOWN -> ScoreAnimationHelper.startTouchY = ev.rawY
			MotionEvent.ACTION_MOVE -> if (!ScoreAnimationHelper.isDecideTouchEvent) {
				val offset = ev.rawY - ScoreAnimationHelper.startTouchY
				ScoreAnimationHelper.isDown = offset > 0
			}
		}
		return super.dispatchTouchEvent(ev)
	}

	override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
		if (ScoreAnimationHelper.touchByParent)
			return true
		return super.onInterceptTouchEvent(ev)
	}

	override fun onTouchEvent(event: MotionEvent?): Boolean {
		return when {
			ScoreAnimationHelper.touchByParent -> {
				doOnTouchListener.invoke(event)
				if (event?.action == MotionEvent.ACTION_UP) {
					ScoreAnimationHelper.touchByRecyclerView = false
					ScoreAnimationHelper.touchByParent = false
					ScoreAnimationHelper.isDown = false
					ScoreAnimationHelper.isDecideTouchEvent = false
				}
				true
			}
			else -> super.onTouchEvent(event)
		}
	}
}