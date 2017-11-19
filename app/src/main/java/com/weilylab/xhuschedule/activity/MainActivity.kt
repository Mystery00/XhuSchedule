package com.weilylab.xhuschedule.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.view.MenuItem
import android.widget.TextView
import com.bumptech.glide.Glide
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ViewPagerAdapter
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.fragment.TableFragment
import com.weilylab.xhuschedule.fragment.TodayFragment
import com.weilylab.xhuschedule.interfaces.RTResponse
import com.weilylab.xhuschedule.service.UpdateService
import com.weilylab.xhuschedule.util.CourseUtil
import com.weilylab.xhuschedule.util.FileUtil
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.Settings
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import vip.mystery0.tools.logs.Logs
import java.io.File

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{
	companion object
	{
		private val TAG = "MainActivity"
	}

	private val retrofit = ScheduleHelper.getRetrofit()
	private lateinit var loadingDialog: ZLoadingDialog
	private var weekList = ArrayList<Course?>()
	private var allList = ArrayList<Course?>()
	private val todayList = ArrayList<Course>()
	private val todayFragment = TodayFragment.newInstance(todayList)
	private val weekFragment = TableFragment.newInstance(weekList, true)
	private val allFragment = TableFragment.newInstance(allList, false)
	private var isRefresh = false

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		loadingDialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_update_cache))
				.setHintTextSize(16F)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			loadingDialog.setLoadingColor(resources.getColor(R.color.colorAccent, null))
			loadingDialog.setHintTextColor(resources.getColor(R.color.colorAccent, null))
		}
		else
		{
			loadingDialog.setLoadingColor(Color.parseColor("#ff4081"))
			loadingDialog.setHintTextColor(Color.parseColor("#ff4081"))
		}

		val toggle = ActionBarDrawerToggle(
				this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		drawer_layout.addDrawerListener(toggle)
		toggle.syncState()

		nav_view.setNavigationItemSelectedListener(this)

		initView()
		updateView()
		if (Settings.isFirstRun)
			showcase()
	}

	override fun onResume()
	{
		super.onResume()
		if (Settings.customBackgroundImg != "")
			Glide.with(this).load(Settings.customBackgroundImg).into(background)
		if (Settings.customHeaderImg != "")
			Glide.with(this).load(Settings.customHeaderImg).into(nav_view.getHeaderView(0).findViewById(R.id.background))
	}

	private fun initView()
	{
		startService(Intent(this, UpdateService::class.java))

		val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
		viewPagerAdapter.addFragment(todayFragment)
		viewPagerAdapter.addFragment(weekFragment)
		viewPagerAdapter.addFragment(allFragment)
		viewpager.offscreenPageLimit = 2
		viewpager.adapter = viewPagerAdapter

		bottomNavigationView.setOnNavigationItemSelectedListener { item ->
			when (item.itemId)
			{
				R.id.bottom_nav_today -> viewpager.currentItem = 0
				R.id.bottom_nav_week -> viewpager.currentItem = 1
				R.id.bottom_nav_all -> viewpager.currentItem = 2
			}
			true
		}
		viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener
		{
			override fun onPageScrollStateChanged(state: Int)
			{
			}

			override fun onPageScrolled(position: Int, positionOffset: Float,
										positionOffsetPixels: Int)
			{
			}

			override fun onPageSelected(position: Int)
			{
				bottomNavigationView.menu.getItem(position).isChecked = true
				toolbar.title = resources.getStringArray(R.array.title_array)[position]
			}
		})
		toolbar.title = resources.getStringArray(R.array.title_array)[0]

		swipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_light,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light)
		swipeRefreshLayout.setOnRefreshListener {
			updateView()
		}

		toolbar.inflateMenu(R.menu.menu_activity_main)
		toolbar.setOnMenuItemClickListener { item ->
			when (item.itemId)
			{
				android.R.id.home ->
				{
					drawer_layout.openDrawer(GravityCompat.START)
					true
				}
				R.id.action_sync ->
				{
					updateData()
					true
				}
				else -> super.onOptionsItemSelected(item)
			}
		}
	}

	fun updateView()
	{
		Observable.create<HashMap<String, ArrayList<Course?>>> { subscriber ->
			val parentFile = File(cacheDir.absolutePath + File.separator + "caches/")
			if (!parentFile.exists())
				parentFile.mkdirs()
			val sharedPreference = getSharedPreferences("cache", Context.MODE_PRIVATE)
			val studentNumber = sharedPreference.getString("studentNumber", "0")
			val studentName = sharedPreference.getString("studentName", "0")
			if (studentNumber == "0" || studentName == "0")
			{
				ScheduleHelper.isLogin = false
				subscriber.onComplete()
				return@create
			}
			ScheduleHelper.isLogin = true
			ScheduleHelper.studentName = studentName
			ScheduleHelper.studentNumber = studentNumber
			val base64Name = FileUtil.filterString(Base64.encodeToString(studentNumber.toByteArray(), Base64.DEFAULT))
			//判断是否有缓存
			val cacheResult = parentFile.listFiles().filter { it.name == base64Name }.size == 1
			if (!cacheResult)
			{
				ScheduleHelper.isCookieAvailable = false
				subscriber.onComplete()
				return@create
			}
			val oldFile = File(parentFile, base64Name)
			if (!oldFile.exists())
			{
				ScheduleHelper.isCookieAvailable = false
				subscriber.onComplete()
				return@create
			}
			val courses = FileUtil.getCoursesFromFile(this@MainActivity, oldFile)
			if (courses.isEmpty())
			{
				ScheduleHelper.isCookieAvailable = false
				subscriber.onComplete()
				return@create
			}
			ScheduleHelper.isCookieAvailable = true
			val allArray = CourseUtil.formatCourses(courses)
			allList.clear()
			allList.addAll(allArray)
			val weekArray = CourseUtil.getWeekCourses(FileUtil.getCoursesFromFile(this@MainActivity, oldFile))
			weekList.clear()
			weekList.addAll(weekArray)
			val todayArray = CourseUtil.getTodayCourses(FileUtil.getCoursesFromFile(this@MainActivity, oldFile))
			todayList.clear()
			todayList.addAll(todayArray)
			subscriber.onComplete()
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : Observer<HashMap<String, ArrayList<Course?>>>
				{
					override fun onSubscribe(d: Disposable)
					{
						swipeRefreshLayout.isRefreshing = true
					}

					override fun onComplete()
					{
						swipeRefreshLayout.isRefreshing = false
						Logs.i(TAG, "onComplete: ")

						if (!ScheduleHelper.isLogin)
						{
							startActivity(Intent(this@MainActivity, LoginActivity::class.java))
							finish()
							return
						}
						val group = nav_view.menu.findItem(R.id.nav_group).subMenu
						group.clear()
						group.add(ScheduleHelper.studentName + "(" + ScheduleHelper.studentNumber + ")")
						if (ScheduleHelper.isCookieAvailable)
						{
							Logs.i(TAG, "onComplete: isCookieAvailable")
							val studentNameTextView: TextView = nav_view.getHeaderView(0).findViewById(R.id.studentName)
							val studentNumberTextView: TextView = nav_view.getHeaderView(0).findViewById(R.id.studentNumber)
							studentNameTextView.text = ScheduleHelper.studentName
							studentNumberTextView.text = ScheduleHelper.studentNumber
							when (todayList.size)
							{
								0 -> bottomNavigationView.menu.findItem(R.id.bottom_nav_today).setIcon(R.drawable.ic_sentiment_very_satisfied)
								1 -> bottomNavigationView.menu.findItem(R.id.bottom_nav_today).setIcon(R.drawable.ic_sentiment_very_satisfied)
								2 -> bottomNavigationView.menu.findItem(R.id.bottom_nav_today).setIcon(R.drawable.ic_sentiment_satisfied)
								3 -> bottomNavigationView.menu.findItem(R.id.bottom_nav_today).setIcon(R.drawable.ic_sentiment_neutral)
								4 -> bottomNavigationView.menu.findItem(R.id.bottom_nav_today).setIcon(R.drawable.ic_sentiment_dissatisfied)
								else -> bottomNavigationView.menu.findItem(R.id.bottom_nav_today).setIcon(R.drawable.ic_sentiment_very_dissatisfied)
							}
							todayFragment.refreshData()
							weekFragment.refreshData()
							allFragment.refreshData()
						}
						else
							updateData()
					}

					override fun onError(e: Throwable)
					{
						e.printStackTrace()
						swipeRefreshLayout.isRefreshing = false
					}

					override fun onNext(map: HashMap<String, ArrayList<Course?>>)
					{
						Logs.i(TAG, "onNext: ")
					}
				})
	}

	private fun updateData()
	{
		Observable.create<HashMap<String, Boolean>> { subscriber ->
			val parentFile = File(cacheDir.absolutePath + File.separator + "caches/")
			val sharedPreference = getSharedPreferences("cache", Context.MODE_PRIVATE)
			val studentNumber = sharedPreference.getString("studentNumber", "0")
			if (studentNumber == "0")
			{
				Logs.i(TAG, "updateData: 学号错误")
				ScheduleHelper.isLogin = false
				startActivity(Intent(this@MainActivity, LoginActivity::class.java))
				finish()
				return@create
			}
			val base64Name = FileUtil.filterString(Base64.encodeToString(studentNumber.toByteArray(), Base64.DEFAULT))
			val service = retrofit.create(RTResponse::class.java)
			val call = service.getContentCall()
			val response = call.execute()
			if (!response.isSuccessful)
			{
				Logs.i(TAG, "updateData: 请求失败")
				val map = HashMap<String, Boolean>()
				map.put("isCookieAvailable", false)
				subscriber.onNext(map)
				subscriber.onComplete()
				return@create
			}
			if (response.body()?.rt == "0")
			{
				Logs.i(TAG, "updateData: Cookie过期")
				val map = HashMap<String, Boolean>()
				map.put("isCookieAvailable", false)
				subscriber.onNext(map)
				subscriber.onComplete()
				return@create
			}
			val newFile = File(parentFile, base64Name + ".temp")
			newFile.createNewFile()
			FileUtil.saveObjectToFile(response.body()?.courses!!, newFile)
			val newMD5 = FileUtil.getMD5(newFile)
			val oldFile = File(parentFile, base64Name)
			var oldMD5 = ""
			if (oldFile.exists())
				oldMD5 = FileUtil.getMD5(oldFile)!!
			val map = HashMap<String, Boolean>()
			if (newMD5 != oldMD5)
			{
				oldFile.delete()
				newFile.renameTo(oldFile)
				map.put("isCookieAvailable", true)
				map.put("isUpdateData", true)
				Logs.i(TAG, "updateData: 数据更新")
			}
			else
			{
				newFile.delete()
				map.put("isCookieAvailable", true)
				map.put("isUpdateData", false)
				Logs.i(TAG, "updateData: 数据未变")
			}
			subscriber.onNext(map)
			subscriber.onComplete()
		}
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : Observer<HashMap<String, Boolean>>
				{
					private var isCookieAvailable = false
					private var isUpdateData = false

					override fun onSubscribe(d: Disposable)
					{
						Logs.i(TAG, "onSubscribe: ")
						loadingDialog.show()
					}

					override fun onNext(t: HashMap<String, Boolean>)
					{
						if (t["isCookieAvailable"] != null)
							isCookieAvailable = t["isCookieAvailable"]!!
						if (t["isUpdateData"] != null)
							isUpdateData = t["isUpdateData"]!!
					}

					override fun onError(e: Throwable)
					{
						e.printStackTrace()
						loadingDialog.dismiss()
						isCookieAvailable = false
					}

					override fun onComplete()
					{
						loadingDialog.dismiss()
						isRefresh = true
						ScheduleHelper.isCookieAvailable = isCookieAvailable
						if (!isCookieAvailable)
						{
							Logs.i(TAG, "onComplete: cookie无效")
							Snackbar.make(coordinatorLayout, R.string.hint_invalid_cookie, Snackbar.LENGTH_LONG)
									.setAction(android.R.string.ok) {
										ScheduleHelper.isLogin = false
										startActivity(Intent(this@MainActivity, LoginActivity::class.java))
										finish()
									}
									.show()
						}
						else
						{
							if (isUpdateData)
								Snackbar.make(coordinatorLayout, R.string.hint_update_data_new, Snackbar.LENGTH_SHORT).show()
							else
								Snackbar.make(coordinatorLayout, R.string.hint_update_data, Snackbar.LENGTH_SHORT).show()
							updateView()
						}
					}
				})
	}

	override fun onBackPressed()
	{
		if (drawer_layout.isDrawerOpen(GravityCompat.START))
		{
			drawer_layout.closeDrawer(GravityCompat.START)
		}
		else
		{
			super.onBackPressed()
		}
	}

	override fun onNavigationItemSelected(item: MenuItem): Boolean
	{
		when (item.itemId)
		{
			R.id.nav_settings ->
			{
				startActivity(Intent(this, SettingsActivity::class.java))
			}
			R.id.nav_logout ->
			{
				val sharedPreference = getSharedPreferences("cache", Context.MODE_PRIVATE)
				sharedPreference.edit()
						.remove("studentName")
						.remove("studentNumber")
						.apply()
				startActivity(Intent(this@MainActivity, LoginActivity::class.java))
				finish()
			}
		}
		drawer_layout.closeDrawer(GravityCompat.START)
		return true
	}

	private fun showcase()
	{
		TapTargetSequence(this)
				.targets(
						TapTarget.forView(bottomNavigationView.findViewById(R.id.bottom_nav_today), getString(R.string.showcase_today)),
						TapTarget.forView(bottomNavigationView.findViewById(R.id.bottom_nav_week), getString(R.string.showcase_week)),
						TapTarget.forView(bottomNavigationView.findViewById(R.id.bottom_nav_all), getString(R.string.showcase_all)),
						TapTarget.forToolbarMenuItem(toolbar, R.id.action_sync, getString(R.string.showcase_sync)))
				.continueOnCancel(true)
				.listener(object : TapTargetSequence.Listener
				{
					override fun onSequenceCanceled(lastTarget: TapTarget?)
					{
					}

					override fun onSequenceFinish()
					{
						Logs.i(TAG, "onSequenceFinish: ")
						Settings.isFirstRun = false
					}

					override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean)
					{
					}
				})
				.start()
	}
}
