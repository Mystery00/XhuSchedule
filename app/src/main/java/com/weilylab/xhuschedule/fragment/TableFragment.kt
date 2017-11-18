package com.weilylab.xhuschedule.fragment

import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
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
import com.weilylab.xhuschedule.util.CourseUtil
import com.weilylab.xhuschedule.util.FileUtil
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
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
			val weekIndexSpinner: Spinner = rootView!!.findViewById(R.id.weekIndexSpinner)
			val weekArray = Array<String>(20, { i -> getString(R.string.course_week_index, i + 1) })
			val spinnerAdapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, weekArray)
			spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
			weekIndexSpinner.adapter = spinnerAdapter
			weekIndexSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
			{
				override fun onNothingSelected(parent: AdapterView<*>?)
				{
				}

				override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int,
											id: Long)
				{
					ScheduleHelper.weekIndex = position + 1
					updateData(weekIndexSpinner)
				}
			}
			weekIndexSpinner.setSelection(ScheduleHelper.weekIndex - 1, true)
			arrowBack.setOnClickListener {
				ScheduleHelper.weekIndex--
				updateData(weekIndexSpinner)
			}
			arrowForward.setOnClickListener {
				ScheduleHelper.weekIndex++
				updateData(weekIndexSpinner)
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

	private fun updateData(weekIndexSpinner: Spinner)
	{
		val loadingDialog = ZLoadingDialog(activity)
				.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_update_cache))
				.setHintTextSize(16F)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			loadingDialog.setLoadingColor(resources.getColor(R.color.colorAccent, null))
			loadingDialog.setHintTextColor(resources.getColor(R.color.colorAccent, null))
		}
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
						loadingDialog.show()
					}

					override fun onNext(t: Array<Course?>)
					{
					}

					override fun onError(e: Throwable)
					{
						e.printStackTrace()
						loadingDialog.dismiss()
					}

					override fun onComplete()
					{
						when (ScheduleHelper.weekIndex)
						{
							1 ->
							{
								rootView!!.findViewById<ImageView>(R.id.imageView_back).visibility = View.GONE
								rootView!!.findViewById<ImageView>(R.id.imageView_forward).visibility = View.VISIBLE
							}
							20 ->
							{
								rootView!!.findViewById<ImageView>(R.id.imageView_back).visibility = View.VISIBLE
								rootView!!.findViewById<ImageView>(R.id.imageView_forward).visibility = View.GONE
							}
							else ->
							{
								rootView!!.findViewById<ImageView>(R.id.imageView_back).visibility = View.VISIBLE
								rootView!!.findViewById<ImageView>(R.id.imageView_forward).visibility = View.VISIBLE
							}
						}
						weekIndexSpinner.setSelection(ScheduleHelper.weekIndex - 1, true)
						adapter.notifyDataSetChanged()
						loadingDialog.dismiss()
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
						rootView!!.findViewById<Spinner>(R.id.weekIndexSpinner).setSelection(ScheduleHelper.weekIndex, true)
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