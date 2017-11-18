package com.weilylab.xhuschedule.fragment

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.TableAdapter
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.util.CourseUtil
import com.weilylab.xhuschedule.util.FileUtil
import com.weilylab.xhuschedule.util.ScheduleHelper
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.logs.Logs
import java.io.File

/**
 * Created by myste.
 */
class TableFragment : Fragment()
{
	companion object
	{
		private val TAG = "TableFragment"

		fun newInstance(list: ArrayList<Course?>, isShowArrow: Boolean): TableFragment
		{
			val bundle = Bundle()
			bundle.putSerializable("list", list)
			bundle.putBoolean("isShowArrow", isShowArrow)
			val fragment = TableFragment()
			fragment.arguments = bundle
			return fragment
		}
	}

	private lateinit var list: ArrayList<Course?>
	private var isShowArrow = false
	private lateinit var adapter: TableAdapter
	private var isReady = false
	private var rootView: View? = null

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		@Suppress("UNCHECKED_CAST")
		list = arguments.getSerializable("list") as ArrayList<Course?>
		isShowArrow = arguments.getBoolean("isShowArrow")
		adapter = TableAdapter(activity, list)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
							  savedInstanceState: Bundle?): View?
	{
		if (rootView == null)
		{
			Logs.i(TAG, "onCreateView: ")
			rootView = inflater.inflate(R.layout.fragment_table, container, false)
			val recyclerView: RecyclerView = rootView!!.findViewById(R.id.recycler_view)
			val linearLayout: LinearLayout = rootView!!.findViewById(R.id.table_nav)
			val arrowLayout: ConstraintLayout = rootView!!.findViewById(R.id.table_arrow)
			if (!isShowArrow)
				arrowLayout.visibility = View.GONE
			val arrowBack: ImageView = rootView!!.findViewById(R.id.imageView_back)
			val arrowForward: ImageView = rootView!!.findViewById(R.id.imageView_forward)
			val weekIndexTextView: TextView = rootView!!.findViewById(R.id.weekIndexTextView)
			weekIndexTextView.text = getString(R.string.course_week_index, ScheduleHelper.weekIndex)
			when (ScheduleHelper.weekIndex)
			{
				1 -> arrowBack.visibility = View.GONE
			}
			arrowBack.setOnClickListener {
				ScheduleHelper.weekIndex--
				updateData(weekIndexTextView)
			}
			arrowForward.setOnClickListener {
				ScheduleHelper.weekIndex++
				updateData(weekIndexTextView)
			}
			recyclerView.layoutManager = GridLayoutManager(activity, 7, GridLayoutManager.VERTICAL, false)
			recyclerView.adapter = adapter
			recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
			{
				override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int)
				{
					super.onScrolled(recyclerView, dx, dy)
					linearLayout.scrollBy(dx, dy)
				}
			})
			isReady = true
		}
		return rootView
	}

	private fun updateData(weekIndexTextView: TextView)
	{
		Observable.create<Array<Course?>> { subscriber ->
			val parentFile = File(activity.cacheDir.absolutePath + File.separator + "caches/")
			val base64Name = FileUtil.filterString(Base64.encodeToString(ScheduleHelper.studentNumber.toByteArray(), Base64.DEFAULT))
			list.clear()
			list.addAll(CourseUtil.getWeekCourses(FileUtil.getCoursesFromFile(activity, File(parentFile, base64Name)), ScheduleHelper.weekIndex))
			subscriber.onComplete()
		}
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : Observer<Array<Course?>>
				{
					override fun onSubscribe(d: Disposable)
					{
					}

					override fun onNext(t: Array<Course?>)
					{
					}

					override fun onError(e: Throwable)
					{
					}

					override fun onComplete()
					{
						weekIndexTextView.text = getString(R.string.course_week_index, ScheduleHelper.weekIndex)
						adapter.notifyDataSetChanged()
					}
				})
	}

	fun refreshData()
	{
		Observable.create<Boolean> { subscriber ->
			while (true)
			{
				if (isReady)
					break
				Thread.sleep(200)
			}
			subscriber.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : Observer<Boolean>
				{
					override fun onComplete()
					{
						adapter.notifyDataSetChanged()
					}

					override fun onSubscribe(d: Disposable)
					{
					}

					override fun onError(e: Throwable)
					{
					}

					override fun onNext(t: Boolean)
					{
					}
				})
	}

	override fun onDestroyView()
	{
		super.onDestroyView()
		if (rootView != null)
			(rootView!!.parent as ViewGroup).removeView(rootView)
	}
}