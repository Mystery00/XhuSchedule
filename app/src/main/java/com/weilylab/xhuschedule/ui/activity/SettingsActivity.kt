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
import com.weilylab.xhuschedule.base.BasePreferenceFragment
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.ui.fragment.settings.*
import com.weilylab.xhuschedule.utils.APPActivityManager
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : XhuBaseActivity(R.layout.activity_settings) {
	companion object {
		private const val INTENT_FRAGMENT = "intent_fragment"
		const val TYPE_ACCOUNT = 31
		const val TYPE_CLASS = 32
		const val TYPE_QUERY_SCORE = 33
		const val TYPE_SETTINGS = 34

		fun intentTo(context: Context?, type: Int) {
			val intent = Intent(context, SettingsActivity::class.java)
			intent.putExtra(INTENT_FRAGMENT, type)
			context?.startActivity(intent)
		}
	}

	private lateinit var accountSettingsFragment: AccountSettingsFragment
	private lateinit var classSettingsFragment: ClassSettingsFragment
	private lateinit var queryScoreFragment: QueryScoreFragment
	private lateinit var settingsPreferenceFragment: SettingsPreferenceFragment

	override fun initView() {
		super.initView()
		supportFragmentManager.beginTransaction()
				.replace(R.id.content_wrapper, getFragment())
				.commit()
		setTitleString()
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
	}

	private fun getFragment(): BasePreferenceFragment {
		return when (intent.getIntExtra(INTENT_FRAGMENT, 0)) {
			TYPE_ACCOUNT -> {
				if (!::accountSettingsFragment.isInitialized)
					accountSettingsFragment = AccountSettingsFragment()
				accountSettingsFragment
			}
			TYPE_CLASS -> {
				if (!::classSettingsFragment.isInitialized)
					classSettingsFragment = ClassSettingsFragment()
				classSettingsFragment
			}
			TYPE_QUERY_SCORE -> {
				if (!::queryScoreFragment.isInitialized)
					queryScoreFragment = QueryScoreFragment()
				queryScoreFragment
			}
			TYPE_SETTINGS -> {
				if (!::settingsPreferenceFragment.isInitialized)
					settingsPreferenceFragment = SettingsPreferenceFragment()
				settingsPreferenceFragment
			}
			else -> throw NullPointerException("null")
		}
	}

	private fun setTitleString() {
		toolbar.title = when (intent.getIntExtra(INTENT_FRAGMENT, 0)) {
			TYPE_ACCOUNT -> getString(R.string.profile_action_account_settings)
			TYPE_CLASS -> getString(R.string.profile_action_class_settings)
			TYPE_QUERY_SCORE -> getString(R.string.profile_action_query_score)
			TYPE_SETTINGS -> getString(R.string.profile_action_software_settings)
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

	override fun onDestroy() {
		super.onDestroy()
		APPActivityManager.finishActivity(this)
	}
}
