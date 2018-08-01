package com.weilylab.xhuschedule.newPackage.ui.custom

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.weilylab.xhuschedule.newPackage.config.APP
import com.zhuangfei.timetable.listener.ISchedule
import vip.mystery0.tools.utils.DensityTools
import androidx.databinding.ViewDataBinding
import com.weilylab.xhuschedule.databinding.ItemCustomDateviewBinding
import com.weilylab.xhuschedule.databinding.ItemCustomDateviewFirstBinding
import com.weilylab.xhuschedule.newPackage.config.DateAdapterHelper
import com.weilylab.xhuschedule.newPackage.utils.CalendarUtil
import com.zhuangfei.timetable.model.ScheduleSupport
import com.zhuangfei.timetable.utils.ColorUtils
import kotlin.collections.ArrayList

class CustomDateAdapter : ISchedule.OnDateBuildListener {
	private val dateAdapterHelper = DateAdapterHelper()

	override fun setBackgroundForLayout(layout: LinearLayout?) {
		layout?.setBackgroundColor(ColorUtils.alphaColor(Color.WHITE, 0.1f))
	}

	override fun setAlpha(alpha: Float) {
	}

	override fun onUpdateDate() {
		val weekDays = ScheduleSupport.getWeekDate()
		dateAdapterHelper.monthString = "${weekDays[0]}\n月"
		dateAdapterHelper.dayString = arrayOf(
				"${weekDays[0]}日",
				"${weekDays[1]}日",
				"${weekDays[2]}日",
				"${weekDays[3]}日",
				"${weekDays[4]}日",
				"${weekDays[5]}日",
				"${weekDays[6]}日")
	}

	override fun getDateViews(mInflate: LayoutInflater, perWidth: Float, height: Int): Array<View?> {
		val heightPx = DensityTools.dp2px(APP.context, 35f)
		val views = arrayOfNulls<View>(8)
		val itemCustomDateviewFirstBinding = ItemCustomDateviewFirstBinding.inflate(mInflate)
		val firstLayoutParams = LinearLayout.LayoutParams(perWidth.toInt(), heightPx)
		itemCustomDateviewFirstBinding.root.layoutParams = firstLayoutParams
		itemCustomDateviewFirstBinding.idWeekMonth.text = dateAdapterHelper.monthString
		itemCustomDateviewFirstBinding.root.setBackgroundColor(dateAdapterHelper.colorArray[0])
		views[0] = itemCustomDateviewFirstBinding.root

		//星期设置
		val weekLayoutParams = LinearLayout.LayoutParams((perWidth * 1.5).toInt(), heightPx)
		for (i in 1..7) {
			val itemCustomDateviewBinding = ItemCustomDateviewBinding.inflate(mInflate)
			itemCustomDateviewBinding.root.layoutParams = weekLayoutParams
			itemCustomDateviewBinding.idWeekDayIndex.text = CalendarUtil.getWeekIndexInString(i)
			itemCustomDateviewBinding.idWeekDay.text = dateAdapterHelper.dayString[i - 1]
			itemCustomDateviewBinding.root.setBackgroundColor(dateAdapterHelper.colorArray[i])
			views[i] = itemCustomDateviewBinding.root
		}
		return views
	}

	override fun onHighLight() {
		for (i in 0..7)
			dateAdapterHelper.colorArray[i] = ColorUtils.alphaColor(Color.BLACK, 0.1f)
		//高亮
		dateAdapterHelper.colorArray[CalendarUtil.getWeekIndex()] = ColorUtils.alphaColor(Color.BLACK, 0.2f)
	}
}