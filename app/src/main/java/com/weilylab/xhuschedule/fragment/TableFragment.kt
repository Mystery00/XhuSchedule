package com.weilylab.xhuschedule.fragment

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.TableAdapter
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.util.CalendarUtil
import com.weilylab.xhuschedule.util.CourseUtil
import com.weilylab.xhuschedule.util.XhuFileUtil
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

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

    fun updateData() {
        val loadingDialog = ZLoadingDialog(activity)
                .setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)
                .setHintText(getString(R.string.hint_dialog_update_cache))
                .setHintTextSize(16F)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            loadingDialog.setLoadingColor(resources.getColor(R.color.colorAccent, null))
            loadingDialog.setHintTextColor(resources.getColor(R.color.colorAccent, null))
        } else {
            loadingDialog.setLoadingColor(Color.parseColor("#ff4081"))
            loadingDialog.setHintTextColor(Color.parseColor("#ff4081"))
        }
        Observable.create<Array<Course?>> { subscriber ->
            val sharedPreference = activity.getSharedPreferences("cache", Context.MODE_PRIVATE)
            val studentNumber = sharedPreference.getString("username", "0")
            val parentFile = File(activity.filesDir.absolutePath + File.separator + "caches/")
            val base64Name = XhuFileUtil.filterString(Base64.encodeToString(studentNumber.toByteArray(), Base64.DEFAULT))
            list.clear()
            list.addAll(CourseUtil.getWeekCourses(XhuFileUtil.getCoursesFromFile(activity, File(parentFile, base64Name)), ScheduleHelper.weekIndex))
            subscriber.onComplete()
        }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Array<Course?>> {
                    override fun onSubscribe(d: Disposable) {
                        loadingDialog.show()
                    }

                    override fun onNext(t: Array<Course?>) {
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        loadingDialog.dismiss()
                    }

                    override fun onComplete() {
                        adapter.notifyDataSetChanged()
                        loadingDialog.dismiss()
                    }
                })
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