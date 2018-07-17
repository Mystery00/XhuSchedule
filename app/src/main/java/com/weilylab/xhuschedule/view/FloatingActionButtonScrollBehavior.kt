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
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator


/**
 * Created by loonggg on 2016/6/22.
 *
 */
class FloatingActionButtonScrollBehavior(context: Context,
                                         attrs: AttributeSet) : FloatingActionButton.Behavior() {
    companion object {
        private val SHOW_HIDE_ANIM_DURATION = 160L
    }

    private var mIsAnimatingOut = false

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout,
                                     child: FloatingActionButton, directTargetChild: View,
                                     target: View, nestedScrollAxes: Int, type: Int): Boolean {
        // 确保是竖直判断的滚动
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes, type)
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton,
                                target: View, dxConsumed: Int, dyConsumed: Int,
                                dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, type)
        if (dyConsumed > 0 && !this.mIsAnimatingOut && child.visibility == View.VISIBLE) {
            animateOut(child)
        } else if (dyConsumed < 0 && child.visibility != View.VISIBLE) {
            animateIn(child)
        }
    }

    private fun animateOut(button: FloatingActionButton) {
        ViewCompat.animate(button)
                .scaleX(0f)
                .scaleY(0f)
                .withLayer()
                .setDuration(SHOW_HIDE_ANIM_DURATION)
                .setInterpolator(LinearOutSlowInInterpolator())
                .setListener(object : ViewPropertyAnimatorListener {
                    override fun onAnimationStart(view: View) {
                        mIsAnimatingOut = true
                    }

                    override fun onAnimationCancel(view: View) {
                        mIsAnimatingOut = false
                    }

                    override fun onAnimationEnd(view: View) {
                        mIsAnimatingOut = false
                        view.visibility = View.INVISIBLE
                    }
                })
                .start()
    }

    private fun animateIn(button: FloatingActionButton) {
        button.visibility = View.VISIBLE
        ViewCompat.animate(button)
                .scaleX(1f)
                .scaleY(1f)
                .withLayer()
                .setDuration(SHOW_HIDE_ANIM_DURATION)
                .setInterpolator(LinearOutSlowInInterpolator())
                .setListener(null)
                .start()
    }
}