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
import com.weilylab.xhuschedule.util.CalendarUtil
import com.weilylab.xhuschedule.util.DensityUtil
import com.weilylab.xhuschedule.util.Settings
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Created by myste.
 */
class TableFragment : Fragment() {
    companion object {
        fun newInstance(list: ArrayList<LinkedList<Course>>): TableFragment {
            val bundle = Bundle()
            bundle.putSerializable("list", list)
            val fragment = TableFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var list: ArrayList<LinkedList<Course>>
    private lateinit var adapter: TableAdapter
    private var isReady = false
    private var rootView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNCHECKED_CAST")
        list = arguments.getSerializable("list") as ArrayList<LinkedList<Course>>
        adapter = TableAdapter(activity, list)
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
            val recyclerView: RecyclerView = rootView!!.findViewById(R.id.recycler_view)
            val linearLayout: LinearLayout = rootView!!.findViewById(R.id.table_nav)
            for (i in 0 until linearLayout.childCount) {
                val layoutParams = linearLayout.getChildAt(i).layoutParams
                layoutParams.height = DensityUtil.dip2px(activity, Settings.customTextHeight.toFloat())
                linearLayout.getChildAt(i).layoutParams = layoutParams
            }
            recyclerView.layoutManager = GridLayoutManager(activity, 7, GridLayoutManager.VERTICAL, false)
            recyclerView.adapter = adapter
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    linearLayout.scrollBy(dx, dy)
                }
            })
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
                        adapter.notifyDataSetChanged()
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
}