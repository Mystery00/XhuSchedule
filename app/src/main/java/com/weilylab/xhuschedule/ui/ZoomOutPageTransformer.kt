/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui

import android.view.View

import androidx.viewpager.widget.ViewPager
import kotlin.math.abs
import kotlin.math.max

class ZoomOutPageTransformer : ViewPager.PageTransformer {
	override fun transformPage(view: View, position: Float) {
		val pageWidth = view.width
		val pageHeight = view.height
		when {
			position < -1 -> // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.alpha = 0f
			position <= 1 -> { // [-1,1]
				// Modify the default slide transition to shrink the page as well
				val scaleFactor = max(MIN_SCALE, 1 - abs(position))
				val verticalMargin = pageHeight * (1 - scaleFactor) / 2
				val horizontalMargin = pageWidth * (1 - scaleFactor) / 2
				if (position < 0)
					view.translationX = horizontalMargin - verticalMargin / 2
				else
					view.translationX = -horizontalMargin + verticalMargin / 2
				// Scale the page down (between MIN_SCALE and 1)
				view.scaleX = scaleFactor
				view.scaleY = scaleFactor
				// Fade the page relative to its size.
				view.alpha = MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)
			}
			else -> // (1,+Infinity]
				// This page is way off-screen to the right.
				view.alpha = 0f
		}
	}

	companion object {
		private const val MIN_SCALE = 0.85f
		private const val MIN_ALPHA = 0.5f
	}
}