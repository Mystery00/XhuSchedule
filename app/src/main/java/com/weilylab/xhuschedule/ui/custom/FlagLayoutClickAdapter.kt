/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.custom

import android.animation.Animator
import android.animation.ObjectAnimator
import com.weilylab.xhuschedule.config.SpaceScheduleHelper
import com.zhuangfei.timetable.TimetableView
import com.zhuangfei.timetable.listener.ISchedule

class FlagLayoutClickAdapter(private val timetableView: TimetableView) : ISchedule.OnFlaglayoutClickListener {

    override fun onFlaglayoutClick(day: Int, start: Int) {
        hideFlagLayout()
        SpaceScheduleHelper.onSpaceScheduleClickListener?.invoke(day, start, true)
    }

    private fun hideFlagLayout() {
        SpaceScheduleHelper.animation?.cancel()
        SpaceScheduleHelper.animation = ObjectAnimator.ofFloat(timetableView.flagLayout(), "alpha", 1F, 0F)
        SpaceScheduleHelper.animation!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                timetableView.hideFlaglayout()
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