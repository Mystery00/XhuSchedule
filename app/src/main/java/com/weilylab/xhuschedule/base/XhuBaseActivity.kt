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

package com.weilylab.xhuschedule.base

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import com.weilylab.xhuschedule.utils.ConfigUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import vip.mystery0.logs.Logs
import vip.mystery0.tools.base.BaseActivity
import vip.mystery0.tools.utils.ActivityManagerTools

abstract class XhuBaseActivity(layoutId: Int?, private val isSetStatusBar: Boolean = true) : BaseActivity(layoutId) {
	private var toast: Toast? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		ActivityManagerTools.instance.addActivity(this)
		super.onCreate(savedInstanceState)
		when (ConfigurationUtil.nightMode) {
			0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
			1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
			2 -> {
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
				if (isSetStatusBar)
					ConfigUtil.setStatusBar(this)
			}
			3 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		ActivityManagerTools.instance.removeActivity(this)
	}

	fun toastMessage(@StringRes stringRes: Int, isShowLong: Boolean = false) = toastMessage(getString(stringRes), isShowLong)

	fun toastMessage(message: String?, isShowLong: Boolean = false) {
		toast?.cancel()
		toast = Toast.makeText(this, message, if (isShowLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT)
		toast?.show()
	}
}