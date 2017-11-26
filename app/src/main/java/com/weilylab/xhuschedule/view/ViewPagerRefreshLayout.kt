/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-21 下午2:26
 */

package com.weilylab.xhuschedule.view

import android.content.Context
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet

/**
 * Created by AItsuki on 2016/1/20.
 *
 */

class ViewPagerRefreshLayout(context: Context,
                             attrs: AttributeSet) : SwipeRefreshLayout(context, attrs) {
    private var startY: Float = 0.toFloat()
    private var startX: Float = 0.toFloat()
    // 记录viewPager是否拖拽的标记
    private var mIsVpDagger: Boolean = false
    private val mTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                // 记录手指按下的位置
                startY = ev.y
                startX = ev.x
                // 初始化标记
                mIsVpDagger = false
            }
            MotionEvent.ACTION_MOVE -> {
                // 如果viewpager正在拖拽中，那么不拦截它的事件，直接return false；
                if (mIsVpDagger) {
                    return false
                }

                // 获取当前手指位置
                val endY = ev.y
                val endX = ev.x
                val distanceX = Math.abs(endX - startX)
                val distanceY = Math.abs(endY - startY)
                // 如果X轴位移大于Y轴位移，那么将事件交给viewPager处理。
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    mIsVpDagger = true
                    return false
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                // 初始化标记
                mIsVpDagger = false
        }
        // 如果是Y轴位移大于X轴，事件交给swipeRefreshLayout处理。
        return super.onInterceptTouchEvent(ev)
    }
}