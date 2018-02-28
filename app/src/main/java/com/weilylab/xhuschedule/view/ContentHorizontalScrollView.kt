/*
 * Created by Mystery0 on 18-2-28 上午5:06.
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
 * Last modified 18-2-28 上午5:06
 */

package com.weilylab.xhuschedule.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.HorizontalScrollView

class ContentHorizontalScrollView : HorizontalScrollView {
    var view: View? = null//联动的View
    var parentScrollView: ViewPager? = null
    private var isScrolledToStart = true
    private var isScrolledToEnd = false
    private var isOnStartEdge = false
    private var isOnEndEdge = false
    private var lastX = 0F

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        //设置控件滚动监听，得到滚动的距离，然后让传进来的view也设置相同的滚动具体
        if (view != null) {
            view!!.scrollTo(l, t)
        }
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
        if (scrollX == 0) {
            isScrolledToStart = clampedX
            isScrolledToEnd = false
        } else {
            isScrolledToStart = false
            isScrolledToEnd = clampedX
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = ev.x
                isOnStartEdge = isScrolledToStart
                isOnEndEdge = isScrolledToEnd
                parentScrollView?.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val distance = ev.x - lastX
                when {
                    isScrolledToStart && distance > 0 && isOnStartEdge ->
                        parentScrollView?.requestDisallowInterceptTouchEvent(false)
                    isScrolledToEnd && distance < 0 && isOnEndEdge ->
                        parentScrollView?.requestDisallowInterceptTouchEvent(false)
                }
                lastX = ev.x
            }
        }
        return super.dispatchTouchEvent(ev)
    }

//    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
////        if (isScrolledToStart || isScrolledToEnd)
////            return false
//        return super.onInterceptTouchEvent(ev)
//    }
//
//    override fun onTouchEvent(ev: MotionEvent?): Boolean {
//        when (ev?.action) {
//            MotionEvent.ACTION_DOWN -> {
//                downX = ev.x
//            }
//            MotionEvent.ACTION_MOVE -> {
//                Logs.i(TAG, "onTouchEvent: ${ev.x}")
//            }
//        }
//        return super.onTouchEvent(ev)
//    }
}