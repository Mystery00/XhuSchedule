/*
 * Created by Mystery0 on 18-2-21 下午9:12.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 18-2-21 下午9:11
 */

package com.weilylab.xhuschedule.activity

import android.os.Bundle
import android.preference.PreferenceActivity
import android.view.MenuItem
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.fragment.*
import com.weilylab.xhuschedule.fragment.settings.*
import com.weilylab.xhuschedule.util.APPActivityManager
import kotlinx.android.synthetic.main.activity_settings.*

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 * See [Android Design: Settings](http://developer.android.com/design/patterns/settings.html)
 * for design guidelines and the [Settings API Guide](http://developer.android.com/guide/topics/ui/settings.html)
 * for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		APPActivityManager.addActivity(this)
		setContentView(R.layout.activity_settings)
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
	}

	/**
	 * {@inheritDoc}
	 */
	override fun onIsMultiPane(): Boolean = false

	/**
	 * This method stops fragment injection in malicious applications.
	 * Make sure to deny any unknown fragments here.
	 */
	override fun isValidFragment(fragmentName: String): Boolean = when (fragmentName) {
		ClassSettingsFragment::class.java.name -> true
		AccountSettingsFragment::class.java.name -> true
		UISettingsFragment::class.java.name -> true
		NotificationFragment::class.java.name -> true
		DeveloperFragment::class.java.name -> true
		InfoSettingsFragment::class.java.name -> true
		else -> false
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			android.R.id.home ->
				onBackPressed()
		}
		return true
	}

	override fun onDestroy() {
		super.onDestroy()
		APPActivityManager.finishActivity(this)
	}
}
