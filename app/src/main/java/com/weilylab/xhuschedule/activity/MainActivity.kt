package com.weilylab.xhuschedule.activity

import android.content.Context
import android.content.Intent
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

	private val retrofit = ScheduleHelper.getInstance().getRetrofit()
	private lateinit var loadingDialog: ZLoadingDialog
	private var weekList = ArrayList<Course?>()
	private var allList = ArrayList<Course?>()
	private val todayList = ArrayList<Course>()
	private val todayFragment = TodayFragment.newInstance(todayList)
	private val weekFragment = TableFragment.newInstance(weekList)
	private val allFragment = TableFragment.newInstance(allList)
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
		loadingDialog.show()

		val toggle = ActionBarDrawerToggle(
				this, drawer_layout, titleTextView, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		drawer_layout.addDrawerListener(toggle)
		toggle.syncState()

		nav_view.setNavigationItemSelectedListener(this)

		initView()
		updateView()
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
			}
		})

		swipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_light,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light)
		swipeRefreshLayout.setOnRefreshListener {
			updateData()
		}
	}

	fun updateView()
	{
		val observer = object : Observer<HashMap<String, ArrayList<Course?>>>
		{
			override fun onSubscribe(d: Disposable)
			{
				loadingDialog.show()
			}

			override fun onComplete()
			{
				loadingDialog.dismiss()
				Logs.i(TAG, "onComplete: ")

				if (!ScheduleHelper.getInstance().isLogin)
				{
					startActivity(Intent(this@MainActivity, LoginActivity::class.java))
					finish()
					return
				}
				nav_view.menu.findItem(R.id.nav_group).subMenu.add(ScheduleHelper.getInstance().studentName + "(" + ScheduleHelper.getInstance().studentNumber + ")")
				if (ScheduleHelper.getInstance().isCookieAvailable)
				{
					val studentNameTextView: TextView = nav_view.getHeaderView(0).findViewById(R.id.studentName)
					val studentNumberTextView: TextView = nav_view.getHeaderView(0).findViewById(R.id.studentNumber)
					studentNameTextView.text = ScheduleHelper.getInstance().studentName
					studentNumberTextView.text = ScheduleHelper.getInstance().studentNumber
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
				loadingDialog.dismiss()
			}

			override fun onNext(map: HashMap<String, ArrayList<Course?>>)
			{
				Logs.i(TAG, "onNext: ")
			}
		}

		val observable = Observable.create<HashMap<String, ArrayList<Course?>>> { subscriber ->
			val parentFile = File(cacheDir.absolutePath + File.separator + "caches/")
			if (!parentFile.exists())
				parentFile.mkdirs()
			val sharedPreference = getSharedPreferences("cache", Context.MODE_PRIVATE)
			val studentNumber = sharedPreference.getString("studentNumber", "0")
			val studentName = sharedPreference.getString("studentName", "0")
			if (studentNumber == "0" || studentName == "0")
			{
				ScheduleHelper.getInstance().isLogin = false
				subscriber.onComplete()
				return@create
			}
			ScheduleHelper.getInstance().isLogin = true
			ScheduleHelper.getInstance().studentName = studentName
			ScheduleHelper.getInstance().studentNumber = studentNumber
			val base64Name = FileUtil.getInstance().filterString(Base64.encodeToString(studentNumber.toByteArray(), Base64.DEFAULT))
			//判断是否有缓存
			val cacheResult = parentFile.listFiles().filter { it.name == base64Name }.size == 1
			if (!cacheResult)
			{
				ScheduleHelper.getInstance().isCookieAvailable = false
				subscriber.onComplete()
				return@create
			}
			val oldFile = File(parentFile, base64Name)
			if (!oldFile.exists())
			{
				ScheduleHelper.getInstance().isCookieAvailable = false
				subscriber.onComplete()
				return@create
			}
			val courses = FileUtil.getInstance().getCoursesFromFile(oldFile)
			if (courses.isEmpty())
			{
				ScheduleHelper.getInstance().isCookieAvailable = false
				subscriber.onComplete()
				return@create
			}
			ScheduleHelper.getInstance().isCookieAvailable = true
			val allArray = CourseUtil.formatCourses(courses)
			allList.clear()
			allList.addAll(allArray)
			val colorSharedPreference = getSharedPreferences("course_color", Context.MODE_PRIVATE)
			courses.forEach {
				val md5 = ScheduleHelper.getInstance().getMD5(it.name)
				var savedColor = colorSharedPreference.getString(md5, "")
				var savedTransparencyColor = colorSharedPreference.getString(md5 + "_trans", "")
				if (savedColor == "")
				{
					savedColor = '#' + ScheduleHelper.getInstance().getRandomColor()
					colorSharedPreference.edit().putString(md5, savedColor).apply()
					it.color = savedColor
				}
				else
					it.color = savedColor
				if (savedTransparencyColor == "")
				{
					savedTransparencyColor = "#33" + savedColor.substring(1, savedColor.length)
					colorSharedPreference.edit().putString(md5 + "_trans", savedTransparencyColor).apply()
					it.transparencyColor = savedTransparencyColor
				}
				else
					it.transparencyColor = savedTransparencyColor
			}
			val weekArray = CourseUtil.getWeekCourses(courses)
			weekList.clear()
			weekList.addAll(weekArray)
			val todayArray = CourseUtil.getTodayCourses(courses)
			todayList.clear()
			todayList.addAll(todayArray)
			subscriber.onComplete()
		}

		observable.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer)
	}

	private fun updateData()
	{
		val observer = object : Observer<Boolean>
		{
			private var isCookieAvailable = false

			override fun onSubscribe(d: Disposable)
			{
				Logs.i(TAG, "onSubscribe: ")
				swipeRefreshLayout.isRefreshing = true
			}

			override fun onNext(t: Boolean)
			{
				Logs.i(TAG, "onNext: " + t)
				isCookieAvailable = t
			}

			override fun onError(e: Throwable)
			{
				e.printStackTrace()
				swipeRefreshLayout.isRefreshing = false
				isCookieAvailable = false
			}

			override fun onComplete()
			{
				swipeRefreshLayout.isRefreshing = false
				isRefresh = true
				ScheduleHelper.getInstance().isCookieAvailable = isCookieAvailable
				if (!isCookieAvailable)
				{
					Logs.i(TAG, "onComplete: cookie无效")
					Snackbar.make(coordinatorLayout, R.string.hint_invalid_cookie, Snackbar.LENGTH_LONG)
							.setAction(android.R.string.ok) {
								ScheduleHelper.getInstance().isLogin = false
								startActivity(Intent(this@MainActivity, LoginActivity::class.java))
								finish()
							}
							.show()
				}
				else
				{
					updateView()
				}
			}
		}

		val observable = Observable.create<Boolean> { subscriber ->
			val parentFile = File(cacheDir.absolutePath + File.separator + "caches/")
			val sharedPreference = getSharedPreferences("cache", Context.MODE_PRIVATE)
			val studentNumber = sharedPreference.getString("studentNumber", "0")
			if (studentNumber == "0")
			{
				Logs.i(TAG, "updateData: 学号错误")
				ScheduleHelper.getInstance().isLogin = false
				startActivity(Intent(this@MainActivity, LoginActivity::class.java))
				finish()
				return@create
			}
			val base64Name = FileUtil.getInstance().filterString(Base64.encodeToString(studentNumber.toByteArray(), Base64.DEFAULT))
			val service = retrofit.create(RTResponse::class.java)
			val call = service.getContentCall()
			val response = call.execute()
			if (!response.isSuccessful)
			{
				Logs.i(TAG, "updateData: 请求失败")
				subscriber.onNext(false)
				subscriber.onComplete()
				return@create
			}
			if (response.body()?.rt == "0")
			{
				Logs.i(TAG, "updateData: Cookie过期")
				subscriber.onNext(false)
				subscriber.onComplete()
				return@create
			}
			val newFile = File(parentFile, base64Name + ".temp")
			newFile.createNewFile()
			val createResult = FileUtil.getInstance().saveObjectToFile(response.body()?.courses!!, newFile)
			subscriber.onNext(createResult)
			val newMD5 = FileUtil.getInstance().getMD5(newFile)
			val oldFile = File(parentFile, base64Name)
			var oldMD5 = ""
			if (oldFile.exists())
				oldMD5 = FileUtil.getInstance().getMD5(oldFile)!!
			if (newMD5 != oldMD5)
			{
				oldFile.delete()
				newFile.renameTo(oldFile)
				Logs.i(TAG, "updateData: 数据更新")
			}
			else
			{
				Logs.i(TAG, "updateData: 数据未变")
				newFile.delete()
			}
			subscriber.onNext(true)
			subscriber.onComplete()
		}

		observable.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer)
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
}
