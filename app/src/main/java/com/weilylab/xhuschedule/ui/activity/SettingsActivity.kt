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

package com.weilylab.xhuschedule.ui.activity

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBasePreferenceFragment
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.ui.fragment.settings.*
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : XhuBaseActivity(R.layout.activity_settings) {
	companion object {
		private const val INTENT_FRAGMENT = "intent_fragment"
		const val TYPE_ACCOUNT = 31
		const val TYPE_CLASS = 32
		const val TYPE_QUERY_SCORE = 33
		const val TYPE_SETTINGS = 34
		const val TYPE_ABOUT = 35

		fun intentTo(context: Context?, type: Int) {
			val intent = Intent(context, SettingsActivity::class.java)
			intent.putExtra(INTENT_FRAGMENT, type)
			context?.startActivity(intent)
		}
	}

	private val accountSettingsFragment: AccountSettingsFragment by lazy { AccountSettingsFragment() }
	private val classSettingsFragment: ClassSettingsFragment by lazy { ClassSettingsFragment() }
	private val queryScoreFragment: QueryScoreFragment by lazy { QueryScoreFragment() }
	private val settingsPreferenceFragment: SettingsPreferenceFragment by lazy { SettingsPreferenceFragment() }
	private val aboutSettingFragment: AboutSettingFragment by lazy { AboutSettingFragment() }

	override fun initView() {
		super.initView()
		supportFragmentManager.beginTransaction()
				.replace(R.id.content_wrapper, getFragment())
				.commit()
		setTitleString()
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
	}

	private fun getFragment(): XhuBasePreferenceFragment {
		return when (intent.getIntExtra(INTENT_FRAGMENT, 0)) {
			TYPE_ACCOUNT -> accountSettingsFragment
			TYPE_CLASS -> classSettingsFragment
			TYPE_QUERY_SCORE -> queryScoreFragment
			TYPE_SETTINGS -> settingsPreferenceFragment
			TYPE_ABOUT -> aboutSettingFragment
			else -> throw NullPointerException("null")
		}
	}

	private fun setTitleString() {
		toolbar.title = when (intent.getIntExtra(INTENT_FRAGMENT, 0)) {
			TYPE_ACCOUNT -> getString(R.string.profile_action_account_settings)
			TYPE_CLASS -> getString(R.string.profile_action_class_settings)
			TYPE_QUERY_SCORE -> getString(R.string.profile_action_query_score)
			TYPE_SETTINGS -> getString(R.string.profile_action_software_settings)
			TYPE_ABOUT -> getString(R.string.screen_category_about)
			else -> getString(R.string.title_activity_settings)
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			android.R.id.home ->
				onBackPressed()
		}
		return true
	}
}
