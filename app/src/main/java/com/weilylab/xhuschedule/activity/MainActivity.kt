package com.weilylab.xhuschedule.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ViewPagerAdapter
import com.weilylab.xhuschedule.classes.ContentRT
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.classes.RT
import com.weilylab.xhuschedule.fragment.TableFragment
import com.weilylab.xhuschedule.fragment.TodayFragment
import com.weilylab.xhuschedule.interfaces.RTResponse
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
import vip.mystery0.tools.logs.Logs
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{
	companion object
	{
		private val TAG = "MainActivity"
	}

	private lateinit var rt: RT
	private val retrofit = ScheduleHelper.getInstance().getRetrofit()
	private lateinit var loadingDialog: ZLoadingDialog
	private var list = ArrayList<Course>()
	private val showList = ArrayList<Course>()
	private val todayFragment = TodayFragment.newInstance(list)//更改为showList
	private var tableFragment = TableFragment.newInstance(list)

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val toggle = ActionBarDrawerToggle(
				this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		drawer_layout.addDrawerListener(toggle)
		toggle.syncState()

		nav_view.setNavigationItemSelectedListener(this)

		initView()
		updateView()
		updateData()
	}

	private fun initView()
	{
		loadingDialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)
				.setHintText("loading......")
				.setHintTextSize(16F)
		val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
		viewPagerAdapter.addFragment(todayFragment, "Today")
		viewPagerAdapter.addFragment(tableFragment, "ALL")
		viewpager.adapter = viewPagerAdapter
		tabLayout.setupWithViewPager(viewpager)
	}

	private fun updateView()
	{
		val observer = object : Observer<HashMap<String, Array<Course>>>
		{
			override fun onSubscribe(d: Disposable)
			{
				loadingDialog.show()
			}

			override fun onComplete()
			{
				loadingDialog.dismiss()
				Logs.i(TAG, "onComplete: " + list.size)
				if (list.size == 0)
				{
					startActivity(Intent(this@MainActivity, LoginActivity::class.java))
					finish()
					return
				}
				val studentNameTextView: TextView = nav_view.getHeaderView(0).findViewById(R.id.studentName)
				val studentNumberTextView: TextView = nav_view.getHeaderView(0).findViewById(R.id.studentNumber)
				studentNameTextView.text = ScheduleHelper.getInstance().studentName
				studentNumberTextView.text = ScheduleHelper.getInstance().studentNumber
				todayFragment.adapter.notifyDataSetChanged()
				tableFragment.adapter.notifyDataSetChanged()
			}

			override fun onError(e: Throwable)
			{
				e.printStackTrace()
				loadingDialog.dismiss()
			}

			override fun onNext(map: HashMap<String, Array<Course>>)
			{
				if (map.containsKey("today"))
				{
					showList.clear()
					showList.addAll(map["today"]!!)
				}
				list.clear()
				list.addAll(map["all"]!!)
				loadingDialog.dismiss()
			}
		}

		val observable = Observable.create<HashMap<String, Array<Course>>> { subscriber ->
			val map = HashMap<String, Array<Course>>()
			val parentFile = File(cacheDir.absolutePath + File.separator + "caches/")
			if (!parentFile.exists())
				parentFile.mkdirs()
			val sharedPreference = getSharedPreferences("cache", Context.MODE_PRIVATE)
			val studentNumber = sharedPreference.getString("studentNumber", "0")
			val studentName = sharedPreference.getString("studentName", "0")
			if (studentNumber == "0" || studentName == "0")
			{
				map.put("all", emptyArray())
				subscriber.onNext(map)
				subscriber.onComplete()
				return@create
			}
			ScheduleHelper.getInstance().studentName = studentName
			ScheduleHelper.getInstance().studentNumber = studentNumber
			val base64Name = FileUtil.getInstance().filterString(Base64.encodeToString(studentNumber.toByteArray(), Base64.DEFAULT))
			//判断是否有缓存
			val cacheResult = parentFile.listFiles().filter { it.name == base64Name }.size == 1
			if (!cacheResult)
			{
				map.put("all", emptyArray())
				subscriber.onNext(map)
				subscriber.onComplete()
				return@create
			}
			val oldFile = File(parentFile, base64Name)
			if (!oldFile.exists())
			{
				map.put("all", emptyArray())
				subscriber.onNext(map)
				subscriber.onComplete()
				return@create
			}
			val gson = Gson()
			val rt = gson.fromJson(InputStreamReader(FileInputStream(oldFile)), ContentRT::class.java)
			map.put("all", rt.courses)
			subscriber.onNext(map)
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
			override fun onSubscribe(d: Disposable)
			{
				Logs.i(TAG, "onSubscribe: ")
			}

			override fun onNext(t: Boolean)
			{
				Logs.i(TAG, "onNext: " + t)
				ScheduleHelper.getInstance().isCookieAvailable = t
			}

			override fun onError(e: Throwable)
			{
				e.printStackTrace()
				ScheduleHelper.getInstance().isCookieAvailable = false
			}

			override fun onComplete()
			{
				Logs.i(TAG, "onComplete: " + ScheduleHelper.getInstance().isCookieAvailable)
				if (!ScheduleHelper.getInstance().isCookieAvailable)
				{
					Logs.i(TAG, "onComplete: cookie无效")
//					startActivity(Intent(this@MainActivity, LoginActivity::class.java))
//					finish()
//					return
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
				subscriber.onNext(false)
				subscriber.onComplete()
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
				return@create
			}
			val newFile = File(parentFile, base64Name + ".temp")
			val createResult = FileUtil.getInstance().saveFile(response.body()?.byteStream(), newFile)
			subscriber.onNext(createResult)
			val newMD5 = FileUtil.getInstance().getMD5(newFile)
			val oldFile = File(parentFile, base64Name)
			var oldMD5 = ""
			if (oldFile.exists())
			{
				oldMD5 = FileUtil.getInstance().getMD5(oldFile)!!
			}
			val gson = Gson()
			rt = gson.fromJson(InputStreamReader(FileInputStream(newFile)), RT::class.java)
			if (rt.rt != "1")
			{
				Logs.i(TAG, "updateData: " + rt.rt)
				subscriber.onNext(false)
				subscriber.onComplete()
				return@create
			}
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
			rt = gson.fromJson(InputStreamReader(FileInputStream(oldFile)), ContentRT::class.java)
			subscriber.onNext(true)
			subscriber.onComplete()
		}

		observable.subscribeOn(Schedulers.io())
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

	override fun onCreateOptionsMenu(menu: Menu): Boolean
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		when (item.itemId)
		{
			R.id.action_settings -> return true
			else -> return super.onOptionsItemSelected(item)
		}
	}

	override fun onNavigationItemSelected(item: MenuItem): Boolean
	{
		// Handle navigation view item clicks here.
		when (item.itemId)
		{
			R.id.nav_camera ->
			{
				// Handle the camera action
			}
			R.id.nav_gallery ->
			{

			}
			R.id.nav_slideshow ->
			{

			}
			R.id.nav_manage ->
			{

			}
			R.id.nav_share ->
			{

			}
			R.id.nav_send ->
			{

			}
		}

		drawer_layout.closeDrawer(GravityCompat.START)
		return true
	}
}
