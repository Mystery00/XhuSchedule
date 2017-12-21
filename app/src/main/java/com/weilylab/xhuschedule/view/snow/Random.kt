/*
 * Created by Mystery0 on 17-12-21 下午10:35.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-21 下午10:35
 */

package com.weilylab.xhuschedule.view.snow

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

internal class Random {

    fun getRandom(lower: Float, upper: Float): Float {
        val min = Math.min(lower, upper)
        val max = Math.max(lower, upper)
        return getRandom(max - min) + min
    }

    fun getRandom(upper: Float): Float {
        return RANDOM.nextFloat() * upper
    }

    fun getRandom(upper: Int): Int {
        return RANDOM.nextInt(upper)
    }

    companion object {
        private val RANDOM = java.util.Random()
    }

}
