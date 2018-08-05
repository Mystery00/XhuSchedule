package com.weilylab.xhuschedule.newPackage.ui.custom

import android.animation.Animator
import android.animation.ObjectAnimator
import com.weilylab.xhuschedule.newPackage.config.SpaceScheduleHelper
import com.zhuangfei.timetable.TimetableView
import com.zhuangfei.timetable.listener.OnSpaceItemClickAdapter

class SpaceItemClickAdapter(private val timetableView: TimetableView) : OnSpaceItemClickAdapter() {
	override fun onSpaceItemClick(day: Int, start: Int) {
		super.onSpaceItemClick(day, start)
		showFlagLayout()
		SpaceScheduleHelper.onSpaceScheduleClickListener?.invoke(day, start, false)
	}

	private fun showFlagLayout() {
		SpaceScheduleHelper.animation?.cancel()
		SpaceScheduleHelper.animation = ObjectAnimator.ofFloat(timetableView.flagLayout(), "alpha", 0F, 1F)
		SpaceScheduleHelper.animation!!.addListener(object : Animator.AnimatorListener {
			override fun onAnimationRepeat(p0: Animator?) {
			}

			override fun onAnimationEnd(p0: Animator?) {
			}

			override fun onAnimationCancel(p0: Animator?) {
			}

			override fun onAnimationStart(p0: Animator?) {
				timetableView.showFlaglayout()
			}
		})
		SpaceScheduleHelper.animation?.start()
	}
}