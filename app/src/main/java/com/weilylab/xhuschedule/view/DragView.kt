/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-22 上午1:25
 */

package com.weilylab.xhuschedule.view

import android.content.Context
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout

/**
 * Created by myste.
 */
class DragView : LinearLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)

    private val viewDragHelper: ViewDragHelper = ViewDragHelper.create(this, 1F, object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View?, pointerId: Int): Boolean = true

        override fun getViewHorizontalDragRange(child: View): Int = measuredWidth - child.measuredWidth

        override fun getViewVerticalDragRange(child: View): Int = measuredHeight - child.measuredHeight

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int = Math.min(Math.max(left, paddingLeft), width - child.width)

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int = Math.min(Math.max(top, paddingTop), height - child.height)
    })

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean = viewDragHelper.shouldInterceptTouchEvent(ev)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        viewDragHelper.processTouchEvent(event)
        return true
    }
}