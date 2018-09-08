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
import com.weilylab.xhuschedule.utils.APPActivityManager
import vip.mystery0.logs.Logs
import vip.mystery0.tools.base.BaseActivity

abstract class XhuBaseActivity(layoutId: Int?) : BaseActivity(layoutId) {
	private var toast: Toast? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
		APPActivityManager.addActivity(this)
		super.onCreate(savedInstanceState)
	}

	override fun onDestroy() {
		super.onDestroy()
		APPActivityManager.finishActivity(this)
	}

	fun toastMessage(@StringRes stringRes: Int, isShowLong: Boolean = false) = toastMessage(getString(stringRes), isShowLong)

	fun toastMessage(message: String?, isShowLong: Boolean = false) {
		toast?.cancel()
		toast = Toast.makeText(this, message, if (isShowLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT)
		toast?.show()
	}
}