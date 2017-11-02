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
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.ScheduleHelper
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import vip.mystery0.tools.logs.Logs
import java.io.File

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{
	companion object
	{
		private val TAG = "MainActivity"
	}

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val toggle = ActionBarDrawerToggle(
				this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		drawer_layout.addDrawerListener(toggle)
		toggle.syncState()

		nav_view.setNavigationItemSelectedListener(this)

		checkCache()
	}

	private fun checkCache()
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
				Logs.i(TAG, "onComplete: ")
				if (!ScheduleHelper.getInstance().isCookieAvailable)
				{
					startActivity(Intent(this@MainActivity, LoginActivity::class.java))
					finish()
					return
				}
			}
		}

		val observable = Observable.create<Boolean> { subscriber ->
			val parentFile = File(cacheDir.absolutePath + File.separator + "caches")
			val sharedPreference = getSharedPreferences("cache", Context.MODE_PRIVATE)
			val studentNumber = sharedPreference.getString("studentNumber", "0")
			val studentName = sharedPreference.getString("studentName", "0")
			if (studentNumber == "0" || studentName == "0")
			{
				subscriber.onNext(false)
				subscriber.onComplete()
			}
			else
			{
				val base64Name = Base64.encodeToString(studentNumber.toByteArray(), Base64.DEFAULT)
//				subscriber.onNext(parentFile.listFiles().filter { it.name == base64Name }.size == 1)
				subscriber.onComplete()
			}
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
