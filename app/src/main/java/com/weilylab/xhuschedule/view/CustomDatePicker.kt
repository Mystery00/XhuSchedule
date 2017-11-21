package com.weilylab.xhuschedule.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView

/**
 * Created by myste.
 * 修改版DatePicker，将宽度强行设置为全充满
 */
class CustomDatePicker : DatePicker {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet,
                defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int,
                defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun init(year: Int, monthOfYear: Int, dayOfMonth: Int,
                      onDateChangedListener: OnDateChangedListener?) {
        try {
            val datePickerContainer = getChildAt(0) as LinearLayout
            val scrollViewContainer = datePickerContainer.getChildAt(1) as ScrollView
            val datePickerDayPicker = (scrollViewContainer.getChildAt(0) as ViewGroup).getChildAt(0)
            val frameLayoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            datePickerContainer.layoutParams = frameLayoutParams
            scrollViewContainer.layoutParams = linearLayoutParams
            datePickerDayPicker.layoutParams = frameLayoutParams
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.init(year, monthOfYear, dayOfMonth, onDateChangedListener)
    }
}