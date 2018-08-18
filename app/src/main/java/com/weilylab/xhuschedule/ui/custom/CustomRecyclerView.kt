package com.weilylab.xhuschedule.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.weilylab.xhuschedule.ui.helper.ScoreAnimationHelper

class CustomRecyclerView : RecyclerView {
	constructor(context: Context) : super(context)
	constructor(context: Context, attributes: AttributeSet?) : super(context, attributes)
	constructor(context: Context, attributes: AttributeSet?, defStyle: Int) : super(context, attributes, defStyle)

	override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
		return when (ev?.action) {
			MotionEvent.ACTION_MOVE -> {
				when {
					!ScoreAnimationHelper.isDecideTouchEvent -> {
						when {
							!canScrollVertically(1) && !canScrollVertically(-1) -> ScoreAnimationHelper.touchByParent = true
							ScoreAnimationHelper.isShowScoreLayout && !canScrollVertically(-1) && ScoreAnimationHelper.isDown -> ScoreAnimationHelper.touchByParent = true
							!ScoreAnimationHelper.isShowScoreLayout && ScoreAnimationHelper.hasData && !ScoreAnimationHelper.isDown -> ScoreAnimationHelper.touchByParent = true
							else -> ScoreAnimationHelper.touchByRecyclerView = true
						}
						ScoreAnimationHelper.isDecideTouchEvent = true
						super.dispatchTouchEvent(ev)
					}
					else -> super.dispatchTouchEvent(ev)
				}
			}
			else -> super.dispatchTouchEvent(ev)
		}
	}

	override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
		if (ScoreAnimationHelper.touchByRecyclerView)
			return true
		return super.onInterceptTouchEvent(e)
	}

	override fun onTouchEvent(e: MotionEvent?): Boolean {
		return when {
			ScoreAnimationHelper.touchByRecyclerView -> {
				super.onTouchEvent(e)
				if (e?.action == MotionEvent.ACTION_UP) {
					ScoreAnimationHelper.touchByRecyclerView = false
					ScoreAnimationHelper.touchByParent = false
					ScoreAnimationHelper.isDown = false
					ScoreAnimationHelper.isDecideTouchEvent = false
				}
				true
			}
			else -> super.onTouchEvent(e)
		}
	}
}