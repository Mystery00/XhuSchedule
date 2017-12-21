/*
 * Created by Mystery0 on 17-12-21 下午10:35.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-21 下午10:33
 */

package com.weilylab.xhuschedule.view.snow

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Part of this code is based upon “Snowfall” by Sam Arbesman, licensed under Creative Commons Attribution-Share Alike 3.0 and GNU GPL license.
 * Work: http://openprocessing.org/visuals/?visualID= 84771
 * License:
 *      http://creativecommons.org/licenses/by-sa/3.0/
 *      http://creativecommons.org/licenses/GPL/2.0/
 *
 * © 2015, Mark Allison. All rights reserved. This article originally appeared on Styling Android.
 *
 * Portions of this page are modifications based on work created and shared by Google and used according to terms described in the Creative Commons 3.0 Attribution License
 */

open class SnowView : View {

    companion object {
        private val NUM_SNOWFLAKES = 150
        private val DELAY = 5
    }

    private var snowflakes: Array<SnowFlake>? = null

    private val runnable = Runnable { invalidate() }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private fun resize(width: Int, height: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        snowflakes = Array(NUM_SNOWFLAKES, { SnowFlake.create(width, height, paint) })
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != oldw || h != oldh) {
            resize(w, h)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (snowFlake in snowflakes!!) {
            snowFlake.draw(canvas)
        }
        handler.postDelayed(runnable, DELAY.toLong())
    }
}