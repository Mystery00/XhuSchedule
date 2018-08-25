package com.weilylab.xhuschedule.ui.custom

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.weilylab.xhuschedule.config.APP
import vip.mystery0.tools.utils.DensityTools
import com.weilylab.xhuschedule.databinding.ItemCustomDateviewBinding
import com.weilylab.xhuschedule.databinding.ItemCustomDateviewFirstBinding
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.zhuangfei.timetable.listener.OnDateBuildAapter
import com.zhuangfei.timetable.model.ScheduleSupport
import com.zhuangfei.timetable.utils.ColorUtils
import java.text.SimpleDateFormat
import java.util.*


class CustomDateAdapter(private val startTime: Long) : OnDateBuildAapter() {
	private lateinit var itemCustomDateviewFirstBinding: ItemCustomDateviewFirstBinding
	private val bindingArray = arrayOfNulls<ItemCustomDateviewBinding>(7)
	private val heightPx = DensityTools.dp2px(APP.context, 35f)

	constructor() : this(Calendar.getInstance().timeInMillis)

	override fun onBuildDayLayout(mInflate: LayoutInflater, pos: Int, width: Int, height: Int): View {
		val itemCustomDateviewBinding = ItemCustomDateviewBinding.inflate(mInflate)
		val weekLayoutParams = LinearLayout.LayoutParams(width, heightPx)
		itemCustomDateviewBinding.root.layoutParams = weekLayoutParams
		bindingArray[pos - 1] = itemCustomDateviewBinding
		return itemCustomDateviewBinding.root
	}

	override fun onBuildMonthLayout(mInflate: LayoutInflater, width: Int, height: Int): View {
		itemCustomDateviewFirstBinding = ItemCustomDateviewFirstBinding.inflate(mInflate)
		val firstLayoutParams = LinearLayout.LayoutParams(width, heightPx)
		itemCustomDateviewFirstBinding.root.layoutParams = firstLayoutParams
		return itemCustomDateviewFirstBinding.root
	}

	override fun onUpdateDate(curWeek: Int, targetWeek: Int) {
		val weekDays = ScheduleSupport.getDateStringFromWeek(curWeek, targetWeek)
		val monthString = "${weekDays[0]}\n月"
		itemCustomDateviewFirstBinding.idWeekMonth.text = monthString
		for (i in 0 until 7) {
			bindingArray[i]!!.idWeekDayIndex.text = CalendarUtil.getWeekIndexInString(i + 1)
			val dayString = "${weekDays[i + 1]}日"
			bindingArray[i]!!.idWeekDay.text = dayString
		}
	}

	override fun onHighLight() {
		val normalColor = ColorUtils.alphaColor(Color.BLACK, 0.1f)
		val highLightColor = ColorUtils.alphaColor(Color.BLACK, 0.2f)
		itemCustomDateviewFirstBinding.root.setBackgroundColor(normalColor)
		bindingArray.forEachIndexed { index, itemCustomDateviewBinding ->
			if (CalendarUtil.getWeekIndex() == index + 1)
				itemCustomDateviewBinding!!.root.setBackgroundColor(highLightColor)
			else
				itemCustomDateviewBinding!!.root.setBackgroundColor(normalColor)
		}
	}

	/**
	 * 计算距离开学的天数
	 *
	 * @return 返回值2种类型，0：已经开学；>0:天数
	 */
	fun whenBeginSchool(): Long {
		val calendar = Calendar.getInstance()
		calendar.timeInMillis = startTime
		val calWeek = ScheduleSupport.timeTransfrom(SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(calendar.time))
		return if (calWeek > 0) {//开学
			0
		} else {
			val seconds = (startTime - System.currentTimeMillis()) / 1000
			seconds / (24 * 3600)
		}
	}
}