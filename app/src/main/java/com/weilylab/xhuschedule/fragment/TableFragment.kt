/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-27 上午3:33
 */

package com.weilylab.xhuschedule.fragment

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.classes.TableLayoutHelper
import com.weilylab.xhuschedule.util.CalendarUtil
import com.weilylab.xhuschedule.util.DensityUtil
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.Settings
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.logs.Logs
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max

/**
 * Created by myste.
 */
class TableFragment : Fragment() {
    companion object {
        private val TAG = "TableFragment"
    }

    private var isReady = false
    private var rootView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_table, container, false)
            val monthView: TextView = rootView!!.findViewById(R.id.view)
            val tableHeader: LinearLayout = rootView!!.findViewById(R.id.table_header)
            tableHeader.getChildAt(CalendarUtil.getWeekIndex() - 1).setBackgroundColor(ContextCompat.getColor(activity, R.color.colorWeekPrimary))
            val calendar = Calendar.getInstance()
            val dayWeek = calendar.get(Calendar.DAY_OF_WEEK)
            if (dayWeek == Calendar.SUNDAY)
                calendar.add(Calendar.DAY_OF_MONTH, -1)
            calendar.firstDayOfWeek = Calendar.MONDAY
            val day = calendar.get(Calendar.DAY_OF_WEEK)
            calendar.add(Calendar.DATE, calendar.firstDayOfWeek - day)
            val month = (calendar.get(Calendar.MONTH) + 1).toString() + "\n月"
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
            val linearLayout: LinearLayout = rootView!!.findViewById(R.id.table_nav)
            for (i in 0 until linearLayout.childCount) {
                val layoutParams = linearLayout.getChildAt(i).layoutParams
                layoutParams.height = DensityUtil.dip2px(activity, Settings.customTextHeight.toFloat())
                linearLayout.getChildAt(i).layoutParams = layoutParams
            }
            val scheduleView: View = rootView!!.findViewById(R.id.table_schedule)
            val layoutParams = scheduleView.layoutParams
            layoutParams.height = DensityUtil.dip2px(activity, Settings.customTextHeight.toFloat() * 11)
            scheduleView.layoutParams = layoutParams
            isReady = true
        }
        return rootView
    }

    fun refreshData(array: Array<Array<LinkedList<Course>>>) {
        Observable.create<Boolean> { subscriber ->
            while (true) {
                if (isReady)
                    break
                Thread.sleep(200)
            }
            subscriber.onComplete()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<Boolean>() {
                    override fun onComplete() {
                        if (rootView != null) {
                            val tableHeader: LinearLayout = rootView!!.findViewById(R.id.table_header)
                            val tableNav: LinearLayout = rootView!!.findViewById(R.id.table_nav)
                            for (i in 0 until tableHeader.childCount) {
                                (tableHeader.getChildAt(i) as TextView).setTextColor(Settings.customTableTextColor)
                                if (CalendarUtil.getWeekIndex() - 1 == i)
                                    tableHeader.getChildAt(i).setBackgroundColor(ContextCompat.getColor(activity, R.color.colorWeekPrimary))
                                else
                                    tableHeader.getChildAt(i).setBackgroundColor(Color.parseColor("#00000000"))
                            }
                            for (i in 0 until tableNav.childCount) {
                                val layoutParams = tableNav.getChildAt(i).layoutParams
                                layoutParams.height = DensityUtil.dip2px(activity, Settings.customTextHeight.toFloat())
                                tableNav.getChildAt(i).layoutParams = layoutParams
                                (tableNav.getChildAt(i) as TextView).setTextColor(Settings.customTableTextColor)
                            }
                        }
                        formatView(array)
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onNext(t: Boolean) {
                    }
                })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (rootView != null)
            (rootView!!.parent as ViewGroup).removeView(rootView)
    }

    private fun formatView(array: Array<Array<LinkedList<Course>>>) {
        val itemWidth = rootView!!.findViewById<LinearLayout>(R.id.table_schedule1).measuredWidth
        val itemHeight = DensityUtil.dip2px(activity, Settings.customTextHeight.toFloat())
        for (day in 0 until 7) {
            val layoutList = ArrayList<TableLayoutHelper>()
            val temp = resources.getIdentifier("table_schedule" + (day + 1), "id", "com.weilylab.xhuschedule")
            val linearLayout: LinearLayout = rootView!!.findViewById(temp)
            linearLayout.removeAllViews()
            for (time in 0 until 11) {
                val linkedList = array[time][day]
                Logs.i(TAG, "formatView: position: $time $day")
                if (linkedList.isEmpty()) {//如果这个位置没有课
                    Logs.i(TAG, "formatView: 没有课")
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
                    Logs.i(TAG, "formatView: 有课并且格子被占用")
                    var tableHelper = TableLayoutHelper()
                    for (i in 0 until layoutList.size) {
                        if (time in layoutList[i].start..layoutList[i].end) {
                            tableHelper = layoutList[i]
                            break
                        }
                    }
                    linkedList.forEach { course ->
                        Logs.i(TAG, "formatView: ${course.name}")
                        Logs.i(TAG, "formatView: tableHelper.end: ${tableHelper.end}")
                        val timeArray = course.time.split('-')
                        tableHelper.end = max(tableHelper.end, timeArray[1].toInt() - 1)
                        Logs.i(TAG, "formatView: tableHelper.end.max: ${tableHelper.end}")
                        tableHelper.viewGroup.addView(getItemView(course, tableHelper.start))
                    }
                    val params = tableHelper.viewGroup.layoutParams
                    params.height = (tableHelper.end - tableHelper.start + 1) * itemHeight
                    tableHelper.viewGroup.layoutParams = params
                } else {//这个格子没有被占用
                    Logs.i(TAG, "formatView: 有课格子没有被占用")
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
                    Logs.i(TAG, "formatView: " + tableHelper.toString())
                    layoutList.add(tableHelper)//将这个布局添加进list
                    linearLayout.addView(viewGroup)
                    val params = viewGroup.layoutParams
                    params.height = maxHeight
                    viewGroup.layoutParams = params
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
        val itemHeight = DensityUtil.dip2px(activity, Settings.customTextHeight.toFloat())
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
        if (course.color == "") {
            course.color = '#' + ScheduleHelper.getRandomColor()
        }
        val gradientDrawable = imageView.background as GradientDrawable
        when (course.type) {
            "-1" -> gradientDrawable.setColor(Color.RED)
            "not" -> {
                textViewName.setTextColor(Color.GRAY)
                textViewTeacher.setTextColor(Color.GRAY)
                textViewLocation.setTextColor(Color.GRAY)
                gradientDrawable.setColor(Color.parseColor("#9AEEEEEE"))
            }
            else -> gradientDrawable.setColor(Color.parseColor('#' + Integer.toHexString(Settings.customTableOpacity) + course.color.substring(1)))
        }
        val timeArray = course.time.split('-')
        val height = (timeArray[1].toInt() - timeArray[0].toInt() + 1) * itemHeight
        val linearLayoutParams = LinearLayout.LayoutParams(0, height, 1F)
        linearLayoutParams.topMargin = (timeArray[0].toInt() - startTime - 1) * itemHeight
        itemView.layoutParams = linearLayoutParams
        return itemView
    }
}