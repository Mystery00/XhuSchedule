/*
 * Created by Mystery0 on 18-2-21 下午9:12.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 18-2-21 下午9:11
 */

package com.weilylab.xhuschedule.view

import android.content.Context
import androidx.customview.widget.ViewDragHelper
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

        override fun tryCaptureView(child: View, pointerId: Int): Boolean = true

        override fun getViewHorizontalDragRange(child: View): Int = measuredWidth - child.measuredWidth

        override fun getViewVerticalDragRange(child: View): Int = measuredHeight - child.measuredHeight

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int = Math.min(Math.max(left, paddingLeft), width - child.width)

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int = Math.min(Math.max(top, paddingTop), height - child.height)
    })

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean = viewDragHelper.shouldInterceptTouchEvent(ev)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewDragHelper.processTouchEvent(event)
        return true
    }
}