/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-27 上午3:33
 */

package com.weilylab.xhuschedule.fragment

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.TableAdapter
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.classes.TableLayoutHelper
import com.weilylab.xhuschedule.util.CalendarUtil
import com.weilylab.xhuschedule.util.DensityUtil
import com.weilylab.xhuschedule.util.Settings
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.max

/**
 * Created by myste.
 */
class TableFragment : Fragment() {
    companion object {
        fun newInstance(list: Array<Array<LinkedList<Course>>>): TableFragment {
            val bundle = Bundle()
            bundle.putSerializable("list", list)
            val fragment = TableFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var list: Array<Array<LinkedList<Course>>>
    //    private lateinit var adapter: TableAdapter
    private var isReady = false
    private var rootView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNCHECKED_CAST")
        list = arguments.getSerializable("list") as Array<Array<LinkedList<Course>>>
//        adapter = TableAdapter(activity, list)
    }

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
//            val recyclerView: RecyclerView = rootView!!.findViewById(R.id.recycler_view)
            val linearLayout: LinearLayout = rootView!!.findViewById(R.id.table_nav)
            for (i in 0 until linearLayout.childCount) {
                val layoutParams = linearLayout.getChildAt(i).layoutParams
                layoutParams.height = DensityUtil.dip2px(activity, Settings.customTextHeight.toFloat())
                linearLayout.getChildAt(i).layoutParams = layoutParams
            }
//            recyclerView.layoutManager = GridLayoutManager(activity, 7, GridLayoutManager.VERTICAL, false)
//            recyclerView.adapter = adapter
//            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
//                    super.onScrolled(recyclerView, dx, dy)
//                    linearLayout.scrollBy(dx, dy)
//                }
//            })
            isReady = true
        }
        return rootView
    }

    fun refreshData() {
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
//                        adapter.notifyDataSetChanged()
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
        val itemHeight = DensityUtil.dip2px(activity, Settings.customTextHeight.toFloat())
        for (day in 0 until 7) {
            val layoutList = ArrayList<TableLayoutHelper>()
            val temp = resources.getIdentifier("table_schedule" + (day + 1), "id", "com.weilylab.xhuschedule")
            val linearLayout: LinearLayout = rootView!!.findViewById(temp)
            for (time in 0 until 11) {
                val linkedList = array[time][day]
                if (linkedList.isEmpty()) {//如果这个位置没有课
                    val textView = View.inflate(activity, R.layout.layout_text_view, null)
                    val params = textView.layoutParams
                    params.height = itemHeight
                    textView.layoutParams = params
                    linearLayout.addView(textView)
                    continue
                }
                //该位置有课
                //判断这个格子是否被占用
                if (isShowInLayout(layoutList, time)) {
                }else{
                    val viewGroup = View.inflate(activity, R.layout.item_linear_layout, null) as LinearLayout
                    var maxHeight = 0
                    linkedList.forEach { course ->
                        //循环确定这个格子的高度
                        val timeArray = course.time.split('-')
                        val courseTime = timeArray[1].toInt() - timeArray[0].toInt() + 1//计算这节课长度
                        maxHeight = max(maxHeight, courseTime * itemHeight)
                    }
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
}