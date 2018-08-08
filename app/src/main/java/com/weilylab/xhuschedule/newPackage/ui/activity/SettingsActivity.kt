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

package com.weilylab.xhuschedule.newPackage.ui.activity

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.preference.PreferenceFragment
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.newPackage.base.XhuBaseActivity
import com.weilylab.xhuschedule.newPackage.ui.fragment.settings.AccountSettingsFragment
import com.weilylab.xhuschedule.newPackage.ui.fragment.settings.ClassSettingsFragment
import com.weilylab.xhuschedule.util.APPActivityManager
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : XhuBaseActivity(R.layout.activity_settings) {
	companion object {
		private const val INTENT_FRAGMENT = "intent_fragment"
		const val TYPE_ACCOUNT = 31
		const val TYPE_CLASS = 32

		fun intentTo(context: Context?, type: Int) {
			val intent = Intent(context, SettingsActivity::class.java)
			intent.putExtra(INTENT_FRAGMENT, type)
			context?.startActivity(intent)
		}
	}

	override fun initView() {
		super.initView()
		fragmentManager.beginTransaction()
				.replace(R.id.content_wrapper, getFragment())
				.commit()
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
	}

	private fun getFragment(): PreferenceFragment {
		return when (intent.getIntExtra(INTENT_FRAGMENT, 0)) {
			TYPE_ACCOUNT -> AccountSettingsFragment()
			TYPE_CLASS -> ClassSettingsFragment()
			else -> throw NullPointerException("null")
		}
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
