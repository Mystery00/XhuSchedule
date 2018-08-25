package com.weilylab.xhuschedule.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.databinding.ItemCourseWeekBinding
import com.weilylab.xhuschedule.databinding.LayoutWeekViewBinding
import com.zhuangfei.timetable.listener.IWeekView
import com.zhuangfei.timetable.listener.OnWeekItemClickedAdapter
import com.zhuangfei.timetable.model.Schedule
import com.zhuangfei.timetable.model.ScheduleEnable
import com.zhuangfei.timetable.model.ScheduleSupport
import com.zhuangfei.timetable.model.WeekViewEnable
import com.zhuangfei.timetable.view.WeekView
import java.util.ArrayList

class CustomWeekView : LinearLayout, WeekViewEnable<CustomWeekView> {
	private var layoutWeekViewBinding: LayoutWeekViewBinding
	private lateinit var courseList: ArrayList<Schedule>
	//当前周
	private var curWeek = 1
	private var preIndex = 1
	//多少项
	private var itemCount = 20

	private var onWeekItemClickedListener: IWeekView.OnWeekItemClickedListener = OnWeekItemClickedAdapter()

	constructor(context: Context) : this(context, null)
	constructor(context: Context, attributes: AttributeSet?) : super(context, attributes) {
		layoutWeekViewBinding = LayoutWeekViewBinding.inflate(LayoutInflater.from(context), this, true)
		layoutWeekViewBinding.nestedScrollView.isSmoothScrollingEnabled = true
	}

	fun callback(onWeekItemClickedListener: IWeekView.OnWeekItemClickedListener): CustomWeekView {
		this.onWeekItemClickedListener = onWeekItemClickedListener
		return this
	}

	override fun curWeek(curWeek: Int): CustomWeekView {
		this.curWeek = if (curWeek < 1) 1 else curWeek
		return this
	}

	override fun itemCount(count: Int): CustomWeekView {
		if (count > 0)
			itemCount = count
		return this
	}

	override fun itemCount(): Int = itemCount

	override fun showView(): CustomWeekView {
		layoutWeekViewBinding.weekViewContainer.removeAllViews()
		for (i in 1..itemCount) {
			val itemCourseWeekBinding = ItemCourseWeekBinding.inflate(LayoutInflater.from(context))
			val weekString = "第${i}周"
			itemCourseWeekBinding.weekTextView.text = weekString
			if (i == curWeek)
				itemCourseWeekBinding.weekTextViewBottom.text = "(本周)"
			itemCourseWeekBinding.perWeekView.setData(dataSource(), i)
			itemCourseWeekBinding.perWeekViewLayout.setOnClickListener {
				layoutWeekViewBinding.weekViewContainer.getChildAt(preIndex - 1).setBackgroundColor(ContextCompat.getColor(context, R.color.app_course_chooseweek_bg))
				layoutWeekViewBinding.weekViewContainer.getChildAt(curWeek - 1).background = ContextCompat.getDrawable(context, R.drawable.weekview_thisweek)
				preIndex = i
				itemCourseWeekBinding.perWeekViewLayout.background = ContextCompat.getDrawable(context, R.drawable.weekview_white)
				onWeekItemClickedListener.onWeekClicked(i)
			}
			layoutWeekViewBinding.weekViewContainer.addView(itemCourseWeekBinding.root)
		}
		if (curWeek in 1..itemCount)
			layoutWeekViewBinding.weekViewContainer.getChildAt(curWeek - 1).background = ContextCompat.getDrawable(context, R.drawable.weekview_thisweek)
		return this
	}

	override fun isShow(isShow: Boolean): CustomWeekView {
		layoutWeekViewBinding.nestedScrollView.visibility = if (isShow) View.VISIBLE else View.GONE
		return this
	}

	override fun dataSource(): List<Schedule> {
		if (!::courseList.isInitialized)
			courseList = ArrayList()
		return courseList
	}

	override fun source(list: List<ScheduleEnable>): CustomWeekView {
		return updateSourceList(ScheduleSupport.transform(list))
	}

	override fun data(scheduleList: List<Schedule>): CustomWeekView {
		return updateSourceList(scheduleList)
	}

	private fun updateSourceList(list: List<Schedule>): CustomWeekView {
		if (::courseList.isInitialized) {
			courseList.clear()
			courseList.addAll(list)
		} else {
			courseList = ArrayList()
			courseList.addAll(list)
		}
		return this
	}

	override fun isShowing(): Boolean = layoutWeekViewBinding.nestedScrollView.visibility == View.VISIBLE

	override fun updateView(): CustomWeekView {
		if (layoutWeekViewBinding.weekViewContainer.childCount == 0)
			return this
		for (i in 0 until layoutWeekViewBinding.weekViewContainer.childCount) {
			val itemCourseWeekBinding = ItemCourseWeekBinding.bind(layoutWeekViewBinding.weekViewContainer.getChildAt(i))
			if (curWeek - 1 == i)
				itemCourseWeekBinding.weekTextViewBottom.text = "(本周)"
			else
				itemCourseWeekBinding.weekTextViewBottom.text = ""
			itemCourseWeekBinding.root.setBackgroundColor(ContextCompat.getColor(context, R.color.app_course_chooseweek_bg))
		}
		if (curWeek in 1..layoutWeekViewBinding.weekViewContainer.childCount)
			layoutWeekViewBinding.weekViewContainer.getChildAt(curWeek - 1).background = ContextCompat.getDrawable(context, R.drawable.weekview_thisweek)
		return this
	}
}