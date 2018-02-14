/*
 * Created by Mystery0 on 18-2-15 上午2:31.
 * Copyright (c) 2018. All Rights reserved.
 *
 * Last modified 18-2-15 上午2:31
 */

package com.weilylab.xhuschedule.view

/*
 *    Copyright (C) 2017.  Aesean
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.RectF
import android.os.Build
import android.support.annotation.RequiresApi
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import android.util.Log
import android.widget.TextView

import java.lang.reflect.Method
import java.util.Hashtable

/**
 * TextViewUtils.
 * [android.support.v7.widget.AppCompatTextViewAutoSizeHelper]
 *
 * @author danny
 * @version 1.0
 * @since 1/17/18
 */
@RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
object TextViewUtils {
    private val TAG = "TextViewUtils"
    private val sTextViewMethodByNameCache = Hashtable<String, Method>()

    private val DURATION: Long = 600       // ms
    private val MIN_DURATION: Long = 100   // ms
    private val MAX_DURATION: Long = 1000  // ms
    private val BASE_HEIGHT: Long = 1000   // px

    // horizontal scrolling is activated.
    private val VERY_WIDE = 1024 * 1024
    private val TEMP_RECT_F = RectF()
    private var sTempTextPaint: TextPaint? = null

    fun setMaxLinesWithAnimation(textView: TextView, maxLine: Int): ValueAnimator? {
        measureTextHeight(textView, textView.text.toString())

        val textHeight = measureTextHeight(textView, textView.text, maxLine)
        if (textHeight < 0) {
            // measure failed. setMaxLines directly.
            textView.maxLines = maxLine
            return null
        }
        val minLines = textView.minLines
        val targetHeight = textHeight + textView.compoundPaddingBottom + textView.compoundPaddingTop
        return animatorToHeight(textView, targetHeight, object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                textView.minLines = minLines
                textView.maxLines = maxLine
            }
        })
    }

    fun animatorToHeight(textView: TextView, h: Int, listener: Animator.AnimatorListener?): ValueAnimator? {
        val height = textView.height
        if (height == h) {
            return null
        }
        val valueAnimator = ValueAnimator()
        valueAnimator.setIntValues(height, h)
        val duration = makeDuration(h, height)
        valueAnimator.duration = duration
        valueAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            textView.height = value
        }
        if (listener != null) {
            valueAnimator.addListener(listener)
        }
        valueAnimator.start()
        return valueAnimator
    }

    private fun makeDuration(h: Int, height: Int): Long {
        val d = (DURATION * (Math.abs(h - height) * 1f / BASE_HEIGHT)).toLong()
        return Math.max(MIN_DURATION, Math.min(d, MAX_DURATION))
    }

    @JvmOverloads
    fun measureTextHeight(textView: TextView, text: CharSequence, maxLines: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) textView.maxLines else Integer.MAX_VALUE): Int {
        if (sTempTextPaint == null) {
            sTempTextPaint = TextPaint()
        }
        sTempTextPaint!!.set(textView.paint)
        sTempTextPaint!!.textSize = textView.textSize

        val targetHeight = measureTextHeight(textView, text, sTempTextPaint!!, maxLines)
        sTempTextPaint!!.reset()
        return targetHeight
    }

    fun measureTextHeight(textView: TextView, text: CharSequence, textPaint: TextPaint, maxLines: Int): Int {
        val horizontallyScrolling = invokeAndReturnWithDefault(textView, "getHorizontallyScrolling", false)!!
        val availableWidth = if (horizontallyScrolling)
            VERY_WIDE
        else
            textView.measuredWidth - textView.totalPaddingLeft
                    - textView.totalPaddingRight
        val availableHeight = (textView.height - textView.compoundPaddingBottom
                - textView.compoundPaddingTop)

        if (availableWidth <= 0 || availableHeight <= 0) {
            return -1
        }
        TEMP_RECT_F.setEmpty()
        TEMP_RECT_F.right = availableWidth.toFloat()
        // TEMP_RECT_F.bottom = availableHeight;
        TEMP_RECT_F.bottom = VERY_WIDE.toFloat()

        // Needs reflection call due to being private.
        val alignment = invokeAndReturnWithDefault(
                textView, "getLayoutAlignment", Layout.Alignment.ALIGN_NORMAL)
        val layout = createStaticLayoutForMeasuringCompat(text, alignment, Math.round(TEMP_RECT_F.right), Integer.MAX_VALUE, textPaint, textView)
        return layout.getLineTop(Math.min(layout.lineCount, maxLines))
    }

    private fun createStaticLayoutForMeasuringCompat(text: CharSequence, alignment: Layout.Alignment?, availableWidth: Int, maxLines: Int, textPaint: TextPaint, textView: TextView): StaticLayout {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createStaticLayoutForMeasuring(text, alignment, availableWidth, maxLines, textPaint, textView)
        } else {
            createStaticLayoutForMeasuringPre23(text, alignment, availableWidth, textPaint, textView)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createStaticLayoutForMeasuring(text: CharSequence, alignment: Layout.Alignment?, availableWidth: Int, maxLines: Int, textPaint: TextPaint, textView: TextView): StaticLayout {
        // Can use the StaticLayout.Builder (along with TextView params added in or after
        // API 23) to construct the layout.
        val textDirectionHeuristic = invokeAndReturnWithDefault(textView, "getTextDirectionHeuristic", TextDirectionHeuristics.FIRSTSTRONG_LTR)

        val layoutBuilder = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, availableWidth)

        return layoutBuilder.setAlignment(alignment)
                .setLineSpacing(textView.lineSpacingExtra, textView.lineSpacingMultiplier)
                .setIncludePad(textView.includeFontPadding)
                .setBreakStrategy(textView.breakStrategy)
                .setHyphenationFrequency(textView.hyphenationFrequency)
                .setMaxLines(if (maxLines == -1) Integer.MAX_VALUE else maxLines)
                .setTextDirection(textDirectionHeuristic)
                .build()
    }

    @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private fun createStaticLayoutForMeasuringPre23(text: CharSequence, alignment: Layout.Alignment?, availableWidth: Int, textPaint: TextPaint, textView: TextView): StaticLayout {
        // Setup defaults.
        var lineSpacingMultiplier = 1.0f
        var lineSpacingAdd = 0.0f
        var includePad = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // Call public methods.
            lineSpacingMultiplier = textView.lineSpacingMultiplier
            lineSpacingAdd = textView.lineSpacingExtra
            includePad = textView.includeFontPadding
        } else {
            // Call private methods and make sure to provide fallback defaults in case something
            // goes wrong. The default values have been inlined with the StaticLayout defaults.
            lineSpacingMultiplier = invokeAndReturnWithDefault(textView,
                    "getLineSpacingMultiplier", lineSpacingMultiplier)!!
            lineSpacingAdd = invokeAndReturnWithDefault(textView,
                    "getLineSpacingExtra", lineSpacingAdd)!!

            includePad = invokeAndReturnWithDefault(textView,
                    "getIncludeFontPadding", includePad)!!
        }

        // The layout could not be constructed using the builder so fall back to the
        // most broad constructor.
        return StaticLayout(text, textPaint, availableWidth,
                alignment,
                lineSpacingMultiplier,
                lineSpacingAdd,
                includePad)
    }


    private fun <T> invokeAndReturnWithDefault(`object`: Any, methodName: String, defaultValue: T): T? {
        var result: T? = null
        var exceptionThrown = false

        try {
            // Cache lookup.
            val method = getTextViewMethod(methodName)
            if (method != null) {

                result = method.invoke(`object`) as T
            }
        } catch (ex: Exception) {
            exceptionThrown = true
            Log.w(TAG, "Failed to invoke TextView#$methodName() method", ex)
        } finally {
            if (result == null && exceptionThrown) {
                result = defaultValue
            }
        }

        return result
    }

    @SuppressLint("PrivateApi")
    private fun getTextViewMethod(methodName: String): Method? {
        try {
            var method: Method? = sTextViewMethodByNameCache[methodName]
            if (method == null) {
                method = TextView::class.java.getDeclaredMethod(methodName)
                if (method != null) {
                    method.isAccessible = true
                    // Cache update.
                    sTextViewMethodByNameCache[methodName] = method
                }
            }
            return method
        } catch (ex: Exception) {
            Log.w(TAG, "Failed to retrieve TextView#$methodName() method", ex)
            return null
        }

    }
}
