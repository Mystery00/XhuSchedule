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
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by myste.
 */
class TableFragment : Fragment() {
    companion object {
        fun newInstance(list: ArrayList<Course?>): TableFragment {
            val bundle = Bundle()
            bundle.putSerializable("list", list)
            val fragment = TableFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var list: ArrayList<Course?>
    private lateinit var adapter: TableAdapter
    private var isReady = false
    private var rootView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNCHECKED_CAST")
        list = arguments.getSerializable("list") as ArrayList<Course?>
        adapter = TableAdapter(activity, list)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_table, container, false)
            val tableHeader: LinearLayout = rootView!!.findViewById(R.id.table_header)
            tableHeader.getChildAt(CalendarUtil.getWeekIndex() - 1).setBackgroundColor(ContextCompat.getColor(activity, R.color.colorWeekPrimary))
            val recyclerView: RecyclerView = rootView!!.findViewById(R.id.recycler_view)
            val linearLayout: LinearLayout = rootView!!.findViewById(R.id.table_nav)
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
                .subscribe(object : Observer<Boolean> {
                    override fun onComplete() {
                        if (rootView != null) {
                            val tableHeader: LinearLayout = rootView!!.findViewById(R.id.table_header)
                            for (i in 0 until tableHeader.childCount) {
                                if (CalendarUtil.getWeekIndex() - 1 == i)
                                    tableHeader.getChildAt(i).setBackgroundColor(ContextCompat.getColor(activity, R.color.colorWeekPrimary))
                                else
                                    tableHeader.getChildAt(i).setBackgroundColor(Color.parseColor("#00000000"))
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onSubscribe(d: Disposable) {
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