/*
 * Created by Mystery0 on 18-2-21 下午9:12.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 18-2-21 下午9:11
 */

package com.weilylab.xhuschedule.fragment

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.activity.MainActivity
import com.weilylab.xhuschedule.classes.baseClass.Course
import com.weilylab.xhuschedule.classes.baseClass.TableLayoutHelper
import com.weilylab.xhuschedule.listener.InfoChangeListener
import com.weilylab.xhuschedule.util.*
import com.weilylab.xhuschedule.view.ContentHorizontalScrollView
import com.weilylab.xhuschedule.view.ContentVerticalScrollView
import kotlinx.android.synthetic.main.content_main.*
import vip.mystery0.logs.Logs
import vip.mystery0.tools.base.BaseFragment
import vip.mystery0.tools.utils.ColorTools
import vip.mystery0.tools.utils.DensityTools
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max

/**
 * Created by myste.
 */
class TableFragment : BaseFragment(R.layout.fragment_tableold) {
	companion object {
		fun newInstance(list: ArrayList<ArrayList<ArrayList<Course>>>): TableFragment {
			val bundle = Bundle()
			bundle.putSerializable(Constants.INTENT_TAG_NAME_LIST, list)
			val fragment = TableFragment()
			fragment.arguments = bundle
			return fragment
		}
	}

	private lateinit var list: ArrayList<ArrayList<ArrayList<Course>>>
	private lateinit var backgroundImageView: ImageView
	private lateinit var monthView: TextView
	private lateinit var tableHeader: LinearLayout
	private lateinit var tableNav: LinearLayout
	private lateinit var contentHorizontalScrollView: ContentHorizontalScrollView
	private lateinit var contentVerticalScrollView: ContentVerticalScrollView
	private lateinit var scheduleView: View
	private var isReady = false

	override fun initView() {
		isReady=true
		backgroundImageView = findViewById(R.id.background)
		monthView = findViewById(R.id.view)
		tableHeader = findViewById(R.id.table_header)
		tableNav = findViewById(R.id.table_nav)
		contentHorizontalScrollView = findViewById(R.id.contentHorizontalScrollView)
		contentVerticalScrollView = findViewById(R.id.contentVerticalScrollView)
		scheduleView = findViewById(R.id.table_schedule)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		@Suppress("UNCHECKED_CAST")
		list = arguments?.getSerializable(Constants.INTENT_TAG_NAME_LIST) as ArrayList<ArrayList<ArrayList<Course>>>
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		ViewUtil.setBackground(context!!, backgroundImageView)
		val tabLayoutParams = tableHeader.layoutParams
		tabLayoutParams.width = ScheduleHelper.scheduleItemWidth * 7
		tableHeader.layoutParams = tabLayoutParams
		for (i in 0 until tableHeader.childCount) {
			val layoutParams = tableHeader.getChildAt(i).layoutParams
			tableHeader.getChildAt(i).layoutParams = layoutParams
		}
		(tableHeader.getChildAt(CalendarUtil.getWeekIndex() - 1) as TextView).setTextColor(ContextCompat.getColor(activity!!, R.color.colorWeekPrimary))
		val calendar = Calendar.getInstance()
		val dayWeek = calendar.get(Calendar.DAY_OF_WEEK)
		if (dayWeek == Calendar.SUNDAY)
			calendar.add(Calendar.DAY_OF_MONTH, -1)
		calendar.firstDayOfWeek = Calendar.MONDAY
		val day = calendar.get(Calendar.DAY_OF_WEEK)
		calendar.add(Calendar.DATE, calendar.firstDayOfWeek - day)
		val month = "${calendar.get(Calendar.MONTH) + 1}\n月"
		monthView.text = month
		for (i in 0 until tableHeader.childCount) {
			val textView = tableHeader.getChildAt(i) as TextView
			val text = if (calendar.get(Calendar.DAY_OF_MONTH) == 1)
				"${textView.text}\n${calendar.get(Calendar.MONTH) + 1}月"
			else
				"${textView.text}\n${calendar.get(Calendar.DAY_OF_MONTH)}日"
			textView.text = text
			calendar.add(Calendar.DAY_OF_MONTH, 1)
		}
		val tableNav: LinearLayout = findViewById(R.id.table_nav)
		for (i in 0 until tableNav.childCount) {
			val itemLayoutParams = tableNav.getChildAt(i).layoutParams
			itemLayoutParams.height = DensityTools.dp2px(activity!!, Settings.customTableItemHeight.toFloat())
			tableNav.getChildAt(i).layoutParams = itemLayoutParams
		}
		val scheduleView: View = findViewById(R.id.table_schedule)
		val scheduleLayoutParams = scheduleView.layoutParams
		scheduleLayoutParams.width = ScheduleHelper.scheduleItemWidth * 7
		scheduleLayoutParams.height = DensityTools.dp2px(activity!!, Settings.customTableItemHeight.toFloat() * 11)
		scheduleView.layoutParams = scheduleLayoutParams
		//滑动联动
		val contentHorizontalScrollView: ContentHorizontalScrollView = findViewById(R.id.contentHorizontalScrollView)
		val contentVerticalScrollView: ContentVerticalScrollView = findViewById(R.id.contentVerticalScrollView)
		contentHorizontalScrollView.isHorizontalScrollBarEnabled = false
		contentVerticalScrollView.isVerticalScrollBarEnabled = false
		contentHorizontalScrollView.view = tableHeader
		contentVerticalScrollView.view = tableNav
		contentHorizontalScrollView.parentScrollView = (activity as MainActivity).viewpager
		contentHorizontalScrollView.setScroll(Settings.customTableItemWidth != -1)
	}

	fun isReady(): Boolean {
		return isReady
	}

	fun updateTableLayout(canScroll: Boolean) {
		ScheduleHelper.checkScreenWidth(activity!!)
		val tabHeaderLayoutParams = tableHeader.layoutParams
		tabHeaderLayoutParams.width = ScheduleHelper.scheduleItemWidth * 7
		tableHeader.layoutParams = tabHeaderLayoutParams
		val scheduleLayoutParams = scheduleView.layoutParams
		scheduleLayoutParams.width = ScheduleHelper.scheduleItemWidth * 7
		scheduleView.layoutParams = scheduleLayoutParams
		contentHorizontalScrollView.setScroll(canScroll)
	}

	fun setBackground() {
		setBackground(0)
	}

	/**
	 * 使用重试机制，每次延时400，重试5次
	 * @param time 当前重试的次数
	 */
	private fun setBackground(time: Int) {
		try {
			ViewUtil.setBackground(context!!, backgroundImageView)
		} catch (e: Exception) {
			if (time > 5)
				e.printStackTrace()
			else {
				Timer().schedule(object : TimerTask() {
					override fun run() {
						setBackground(time + 1)
					}
				}, 400)
			}
		}
	}

	fun refreshData() {
		for (i in 0 until tableNav.childCount) {
			val tableNavLayoutParams = tableNav.getChildAt(i).layoutParams
			tableNavLayoutParams.height = DensityTools.dp2px(activity!!, Settings.customTableItemHeight.toFloat())
			tableNav.getChildAt(i).layoutParams = tableNavLayoutParams
			(tableNav.getChildAt(i) as TextView).setTextColor(ContextCompat.getColor(activity!!, R.color.schedule_head_text_color))
		}
		formatView()
	}

	private fun formatView() {
		val itemHeight = DensityTools.dp2px(activity!!, Settings.customTableItemHeight.toFloat() + 0.4F)
		val firstWeekOfTerm = Settings.firstWeekOfTerm
		val date = firstWeekOfTerm.split('-')
		val calendar = Calendar.getInstance()
		calendar.set(date[0].toInt(), date[1].toInt(), date[2].toInt(), 0, 0, 0)
		calendar.add(Calendar.WEEK_OF_YEAR, ScheduleHelper.weekIndex - 1)
		val dayWeek = calendar.get(Calendar.DAY_OF_WEEK)
		if (dayWeek == Calendar.SUNDAY)
			calendar.add(Calendar.DAY_OF_MONTH, -1)
		calendar.firstDayOfWeek = Calendar.MONDAY
		val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
		calendar.add(Calendar.DATE, calendar.firstDayOfWeek - dayOfWeek)
		val headerArray = context!!.resources.getStringArray(R.array.table_header)
		val month = "${calendar.get(Calendar.MONTH) + 1}\n月"
		findViewById<TextView>(R.id.view).text = month
		for (day in 0 until 7) {
			val headerTextView = tableHeader.getChildAt(day) as TextView
			val text = if (calendar.get(Calendar.DAY_OF_MONTH) == 1)
				"${headerArray[day]}\n${calendar.get(Calendar.MONTH) + 1}月"
			else
				"${headerArray[day]}\n${calendar.get(Calendar.DAY_OF_MONTH)}日"
			headerTextView.text = text
			if (CalendarUtil.getWeekIndex() - 1 == day)
				headerTextView.setTextColor(ContextCompat.getColor(context!!, R.color.colorWeekIndex))
			calendar.add(Calendar.DAY_OF_MONTH, 1)
			val layoutList = ArrayList<TableLayoutHelper>()
			val temp = resources.getIdentifier("table_schedule" + (day + 1), "id", "com.weilylab.xhuschedule")
			val linearLayout: LinearLayout = findViewById(temp)
			linearLayout.removeAllViews()
			for (time in 0 until 11) {
				try {
					val linkedList = list[time][day]
					if (linkedList.isEmpty()) {//如果这个位置没有课
						if (isShowInLayout(layoutList, time))//如果格子被占用，直接继续循环
							continue
						val textView = LayoutInflater.from(activity).inflate(R.layout.layout_text_view, null)
						linearLayout.addView(textView)
						val params = textView.layoutParams
						params.height = itemHeight
						textView.layoutParams = params
						continue
					}
					//该位置有课
					//判断这个格子是否被占用
					if (isShowInLayout(layoutList, time)) {
						var tableHelper = TableLayoutHelper()
						for (i in 0 until layoutList.size) {
							if (time in layoutList[i].start..layoutList[i].end) {
								tableHelper = layoutList[i]
								break
							}
						}
						linkedList.forEach { course ->
							val timeArray = course.time.split('-')
							tableHelper.end = max(tableHelper.end, timeArray[1].toInt() - 1)
							tableHelper.viewGroup.addView(getItemView(course, tableHelper.start))
						}
						val params = tableHelper.viewGroup.layoutParams
						params.height = (tableHelper.end - tableHelper.start + 1) * itemHeight
						tableHelper.viewGroup.layoutParams = params
					} else {//这个格子没有被占用
						val view = LayoutInflater.from(activity).inflate(R.layout.item_linear_layout, null)
						val viewGroup: LinearLayout = view.findViewById(R.id.linearLayout)
						var maxHeight = 0
						linkedList.forEach { course ->
							//循环确定这个格子的高度
							val timeArray = course.time.split('-')
							val courseTime = timeArray[1].toInt() - timeArray[0].toInt() + 1//计算这节课长度
							maxHeight = max(maxHeight, courseTime * itemHeight)
							viewGroup.addView(getItemView(course, time))
						}
						val tableHelper = TableLayoutHelper()
						tableHelper.start = time
						tableHelper.end = maxHeight / itemHeight + time - 1
						tableHelper.viewGroup = viewGroup
						layoutList.add(tableHelper)//将这个布局添加进list
						linearLayout.addView(viewGroup)
						val params = viewGroup.layoutParams
						params.height = maxHeight
						viewGroup.layoutParams = params
					}
				} catch (e: Exception) {
					Logs.wtf("formatView: ", e)
				}
			}
		}
	}

	private fun isShowInLayout(list: ArrayList<TableLayoutHelper>, itemIndex: Int): Boolean {
		list.forEach {
			if (itemIndex in it.start..it.end)
				return true
		}
		return false
	}

	private fun getItemView(course: Course, startTime: Int): View {
		val itemHeight = DensityTools.dp2px(activity!!, Settings.customTableItemHeight.toFloat() + 0.4F)
		val itemView = View.inflate(activity, R.layout.item_widget_table, null)
		val imageView: ImageView = itemView.findViewById(R.id.imageView)
		val textViewName: TextView = itemView.findViewById(R.id.textView_name)
		val textViewTeacher: TextView = itemView.findViewById(R.id.textView_teacher)
		val textViewLocation: TextView = itemView.findViewById(R.id.textView_location)
		val textSize = Settings.customTextSize
		textViewName.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
		textViewTeacher.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
		textViewLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
		textViewName.text = course.name
		textViewTeacher.text = course.teacher
		textViewLocation.text = course.location
		textViewName.setTextColor(Settings.customTableTextColor)
		textViewTeacher.setTextColor(Settings.customTableTextColor)
		textViewLocation.setTextColor(Settings.customTableTextColor)
		val gradientDrawable = imageView.background as GradientDrawable
		when (course.type) {
			Constants.COURSE_TYPE_ERROR -> gradientDrawable.setColor(Color.RED)
			Constants.COURSE_TYPE_NOT -> {
				textViewName.setTextColor(Color.GRAY)
				textViewTeacher.setTextColor(Color.GRAY)
				textViewLocation.setTextColor(Color.GRAY)
				gradientDrawable.setColor(Color.parseColor("#9AEEEEEE"))
			}
			else -> gradientDrawable.setColor(ColorTools.parseColor(course.color, Settings.customTableOpacity))

		}
		val timeArray = course.time.split('-')
		val num = timeArray[1].toInt() - timeArray[0].toInt() + 1
		val height = num * itemHeight
		val linearLayoutParams = LinearLayout.LayoutParams(0, height, 1F)
		linearLayoutParams.topMargin = (timeArray[0].toInt() - startTime - 1) * itemHeight
		itemView.layoutParams = linearLayoutParams
		itemView.setOnClickListener {
			ViewUtil.showAlertDialog(context!!, course, object : InfoChangeListener {
				override fun onChange() {
					(activity as MainActivity).updateAllView()
				}
			})
		}
		return itemView
	}
}