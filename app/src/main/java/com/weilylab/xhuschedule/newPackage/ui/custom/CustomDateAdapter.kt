package com.weilylab.xhuschedule.newPackage.ui.custom

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.weilylab.xhuschedule.newPackage.config.APP
import vip.mystery0.tools.utils.DensityTools
import com.weilylab.xhuschedule.databinding.ItemCustomDateviewBinding
import com.weilylab.xhuschedule.databinding.ItemCustomDateviewFirstBinding
import com.weilylab.xhuschedule.newPackage.config.DateAdapterHelper
import com.weilylab.xhuschedule.newPackage.utils.CalendarUtil
import com.zhuangfei.timetable.listener.OnDateBuildAapter
import com.zhuangfei.timetable.model.ScheduleSupport
import com.zhuangfei.timetable.utils.ColorUtils

class CustomDateAdapter : OnDateBuildAapter() {
	private val dateAdapterHelper = DateAdapterHelper()
	private val heightPx = DensityTools.dp2px(APP.context, 35f)

	override fun onBuildDayLayout(mInflate: LayoutInflater, pos: Int, width: Int, height: Int): View {
		val itemCustomDateviewBinding = ItemCustomDateviewBinding.inflate(mInflate)
		val weekLayoutParams = LinearLayout.LayoutParams(width, heightPx)
		itemCustomDateviewBinding.root.layoutParams = weekLayoutParams
		itemCustomDateviewBinding.idWeekDayIndex.text = CalendarUtil.getWeekIndexInString(pos)
		itemCustomDateviewBinding.idWeekDay.text = dateAdapterHelper.dayString[pos - 1]
		itemCustomDateviewBinding.root.setBackgroundColor(dateAdapterHelper.colorArray[pos])
		return itemCustomDateviewBinding.root
	}

	override fun onBuildMonthLayout(mInflate: LayoutInflater, width: Int, height: Int): View {
		val itemCustomDateviewFirstBinding = ItemCustomDateviewFirstBinding.inflate(mInflate)
		val firstLayoutParams = LinearLayout.LayoutParams(width, heightPx)
		itemCustomDateviewFirstBinding.root.layoutParams = firstLayoutParams
		itemCustomDateviewFirstBinding.idWeekMonth.text = dateAdapterHelper.monthString
		itemCustomDateviewFirstBinding.root.setBackgroundColor(dateAdapterHelper.colorArray[0])
		return itemCustomDateviewFirstBinding.root
	}

	override fun onUpdateDate(curWeek: Int, targetWeek: Int) {
		val weekDays = ScheduleSupport.getDateStringFromWeek(curWeek, targetWeek)
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

	override fun onHighLight() {
		for (i in 0..7)
			dateAdapterHelper.colorArray[i] = ColorUtils.alphaColor(Color.BLACK, 0.1f)
		//高亮
		dateAdapterHelper.colorArray[CalendarUtil.getWeekIndex()] = ColorUtils.alphaColor(Color.BLACK, 0.2f)
	}
}