/*
 * Created by Mystery0 on 17-12-21 下午10:35.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-21 下午10:34
 */

package com.weilylab.xhuschedule.view.snow

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point

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

internal class SnowFlake(private val random: Random, private val position: Point, private var angle: Float, private val increment: Float, private val flakeSize: Float, private val paint: Paint) {

    private fun move(width: Int, height: Int) {
        val x = position.x + increment * Math.cos(angle.toDouble())
        val y = position.y + increment * Math.sin(angle.toDouble())

        angle += random.getRandom(-ANGLE_SEED, ANGLE_SEED) / ANGLE_DIVISOR

        position.set(x.toInt(), y.toInt())

        if (!isInside(width, height)) {
            reset(width)
        }
    }

    private fun isInside(width: Int, height: Int): Boolean {
        val x = position.x
        val y = position.y
        return x >= -flakeSize - 1 && x + flakeSize <= width && y >= -flakeSize - 1 && y - flakeSize < height
    }

    private fun reset(width: Int) {
        position.x = random.getRandom(width)
        position.y = (-flakeSize - 1).toInt()
        angle = random.getRandom(ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE
    }

    fun draw(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height
        move(width, height)
        canvas.drawCircle(position.x.toFloat(), position.y.toFloat(), flakeSize, paint)
    }

    companion object {
        private val ANGE_RANGE = 0.1f
        private val HALF_ANGLE_RANGE = ANGE_RANGE / 2f
        private val HALF_PI = Math.PI.toFloat() / 2f
        private val ANGLE_SEED = 25f
        private val ANGLE_DIVISOR = 10000f
        private val INCREMENT_LOWER = 2f
        private val INCREMENT_UPPER = 4f
        private val FLAKE_SIZE_LOWER = 7f
        private val FLAKE_SIZE_UPPER = 20f

        fun create(width: Int, height: Int, paint: Paint): SnowFlake {
            val random = Random()
            val x = random.getRandom(width)
            val y = random.getRandom(height)
            val position = Point(x, y)
            val angle = random.getRandom(ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE
            val increment = random.getRandom(INCREMENT_LOWER, INCREMENT_UPPER)
            val flakeSize = random.getRandom(FLAKE_SIZE_LOWER, FLAKE_SIZE_UPPER)
            return SnowFlake(random, position, angle, increment, flakeSize, paint)
        }
    }
}
