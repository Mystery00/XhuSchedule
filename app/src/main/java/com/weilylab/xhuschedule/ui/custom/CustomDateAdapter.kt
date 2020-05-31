package com.weilylab.xhuschedule.ui.custom

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.weilylab.xhuschedule.databinding.ItemCustomDateviewBinding
import com.weilylab.xhuschedule.databinding.ItemCustomDateviewFirstBinding
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.CalendarUtil.parseCalendar
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.zhuangfei.timetable.listener.OnDateBuildAapter
import com.zhuangfei.timetable.utils.ColorUtils
import vip.mystery0.tools.utils.dpTopx

class CustomDateAdapter : OnDateBuildAapter() {
	private lateinit var itemCustomDateViewFirstBinding: ItemCustomDateviewFirstBinding
	private val bindingArray = arrayOfNulls<ItemCustomDateviewBinding>(7)
	private val heightPx = dpTopx(35F)

	override fun onInit(layout: LinearLayout?, alpha: Float) {
	}

	override fun onBuildDayLayout(mInflate: LayoutInflater, pos: Int, width: Int, height: Int): View {
		val itemCustomDateViewFirstBinding = ItemCustomDateviewBinding.inflate(mInflate)
		val weekLayoutParams = LinearLayout.LayoutParams(width, heightPx)
		itemCustomDateViewFirstBinding.root.layoutParams = weekLayoutParams
		bindingArray[pos - 1] = itemCustomDateViewFirstBinding
		return itemCustomDateViewFirstBinding.root
	}

	override fun onBuildMonthLayout(mInflate: LayoutInflater, width: Int, height: Int): View {
		itemCustomDateViewFirstBinding = ItemCustomDateviewFirstBinding.inflate(mInflate)
		val firstLayoutParams = LinearLayout.LayoutParams(width, heightPx)
		itemCustomDateViewFirstBinding.root.layoutParams = firstLayoutParams
		return itemCustomDateViewFirstBinding.root
	}

	override fun onUpdateDate(curWeek: Int, targetWeek: Int) {
		val dateString =
				if (ConfigurationUtil.isCustomStartTime)
					ConfigurationUtil.customStartTime
				else
					ConfigurationUtil.startTime
		var nowWeek = CalendarUtil.getTrueWeek(dateString.parseCalendar())
		if (nowWeek >= 0) nowWeek++
		val weekDays = CalendarUtil.getDateStringFromWeek(nowWeek, if (targetWeek == 0) nowWeek else targetWeek)
		itemCustomDateViewFirstBinding.idWeekMonth.text = weekDays[0]
		for (i in 0 until 7) {
			bindingArray[i]!!.idWeekDayIndex.text = CalendarUtil.getWeekIndexInString(i + 1)
			bindingArray[i]!!.idWeekDay.text = weekDays[i + 1]
		}
	}

	override fun onHighLight() {
		val highLightColor = ColorUtils.alphaColor(Color.BLACK, 0.2f)
		bindingArray.forEachIndexed { index, itemCustomDateviewBinding ->
			if (CalendarUtil.getWeekIndex() == index + 1)
				itemCustomDateviewBinding!!.root.setBackgroundColor(highLightColor)
			else
				itemCustomDateviewBinding!!.root.setBackgroundColor(Color.TRANSPARENT)
		}
	}
}