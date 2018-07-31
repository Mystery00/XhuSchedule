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
import com.weilylab.xhuschedule.newPackage.utils.CalendarUtil
import com.zhuangfei.timetable.model.ScheduleSupport
import com.zhuangfei.timetable.utils.ColorUtils
import kotlin.collections.ArrayList

class CustomDateAdapter : ISchedule.OnDateBuildListener {
	private val bindingList = ArrayList<ViewDataBinding>()

	override fun setBackgroundForLayout(layout: LinearLayout?) {
		layout?.setBackgroundColor(ColorUtils.alphaColor(Color.WHITE, 0.1f))
	}

	override fun setAlpha(alpha: Float) {
	}

	override fun onUpdateDate() {
		val weekDays = ScheduleSupport.getWeekDate()
		val monthString = "${weekDays[0]}\n月"
		(bindingList[0] as ItemCustomDateviewFirstBinding).idWeekMonth.text = monthString
		for (i in 1..7) {
			val dayString = "${weekDays[i]}日"
			(bindingList[i] as ItemCustomDateviewBinding).idWeekDay.text = dayString
		}
	}

	override fun getDateViews(mInflate: LayoutInflater, perWidth: Float, height: Int): Array<View?> {
		val weekDays = ScheduleSupport.getWeekDate()
		val heightPx = DensityTools.dp2px(APP.context, 35f)
		val views = arrayOfNulls<View>(8)
		val itemCustomDateviewFirstBinding = ItemCustomDateviewFirstBinding.inflate(mInflate)
		val firstLayoutParams = LinearLayout.LayoutParams(perWidth.toInt(), heightPx)
		itemCustomDateviewFirstBinding.root.layoutParams = firstLayoutParams
		val monthString = "${weekDays[0]}\n月"
		itemCustomDateviewFirstBinding.idWeekMonth.text = monthString
		bindingList.add(itemCustomDateviewFirstBinding)
		views[0] = itemCustomDateviewFirstBinding.root

		//星期设置
		val weekLayoutParams = LinearLayout.LayoutParams((perWidth * 1.5).toInt(), heightPx)
		for (i in 1..7) {
			val dayString = "${weekDays[i]}日"
			val itemCustomDateviewBinding = ItemCustomDateviewBinding.inflate(mInflate)
			itemCustomDateviewBinding.root.layoutParams = weekLayoutParams
			itemCustomDateviewBinding.idWeekDayIndex.text = CalendarUtil.getWeekIndexInString(i)
			itemCustomDateviewBinding.idWeekDay.text = dayString
			bindingList.add(itemCustomDateviewBinding)
			views[i] = itemCustomDateviewBinding.root
		}
		return views
	}

	override fun onHighLight() {
		val color = ColorUtils.alphaColor(Color.BLACK, 0.1f)
		for (i in 0..7)
			bindingList[i].root.setBackgroundColor(color)

		//高亮
		val weekIndex = CalendarUtil.getWeekIndex()
		bindingList[weekIndex].root.setBackgroundColor(ColorUtils.alphaColor(Color.BLACK, 0.2f))
	}
}