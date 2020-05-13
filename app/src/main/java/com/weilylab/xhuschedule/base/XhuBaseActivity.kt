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

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.utils.ConfigUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import vip.mystery0.tools.base.BaseActivity

abstract class XhuBaseActivity(layoutId: Int?, private val isSetStatusBar: Boolean = true) : BaseActivity(layoutId) {
	private var snackbar: Snackbar? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (ConfigurationUtil.nightMode == 2 && isSetStatusBar)
			ConfigUtil.setStatusBar(this)
	}

	fun toast(throwable: Throwable?) = toast(throwable?.message)
	fun toastLong(throwable: Throwable?) = toastLong(throwable?.message)

	fun snackbar(@StringRes stringRes: Int) = snackBarMessage(stringRes, {}, Snackbar.LENGTH_SHORT)
	fun snackbar(message: String) = snackBarMessage(message, {}, Snackbar.LENGTH_SHORT)
	fun snackbarLong(@StringRes stringRes: Int) = snackBarMessage(stringRes, {}, Snackbar.LENGTH_LONG)
	fun snackbarLong(message: String) = snackBarMessage(message, {}, Snackbar.LENGTH_LONG)
	fun snackBarMessage(@StringRes stringId: Int, doOther: Snackbar.() -> Unit, @BaseTransientBottomBar.Duration duration: Int) = snackBarMessage(getString(stringId), doOther, duration)
	fun snackBarMessage(message: String, doOther: Snackbar.() -> Unit, @BaseTransientBottomBar.Duration duration: Int) {
		snackbar?.dismiss()
		snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), message, duration)
		doOther.invoke(snackbar!!)
		snackbar!!.show()
	}

	fun buildDialog(@StringRes stringRes: Int,
					type: Z_TYPE = Z_TYPE.SINGLE_CIRCLE,
					textSize: Float = 16F,
					cancelOnTouchOutside: Boolean = false,
					@ColorInt dialogBackgroundColor: Int = ContextCompat.getColor(this, R.color.colorWhiteBackground),
					@ColorInt loadingColor: Int = ContextCompat.getColor(this, R.color.colorAccent),
					@ColorInt textColor: Int = ContextCompat.getColor(this, R.color.colorAccent)): Dialog = buildDialog(getString(stringRes), type, textSize, cancelOnTouchOutside, dialogBackgroundColor, loadingColor, textColor)

	fun buildDialog(text: String,
					type: Z_TYPE = Z_TYPE.SINGLE_CIRCLE,
					textSize: Float = 16F,
					cancelOnTouchOutside: Boolean = false,
					@ColorInt dialogBackgroundColor: Int = ContextCompat.getColor(this, R.color.colorWhiteBackground),
					@ColorInt loadingColor: Int = ContextCompat.getColor(this, R.color.colorAccent),
					@ColorInt textColor: Int = ContextCompat.getColor(this, R.color.colorAccent)): Dialog =
			ZLoadingDialog(this)
					.setLoadingBuilder(type)
					.setHintText(text)
					.setHintTextSize(textSize)
					.setCanceledOnTouchOutside(cancelOnTouchOutside)
					.setDialogBackgroundColor(dialogBackgroundColor)
					.setLoadingColor(loadingColor)
					.setHintTextColor(textColor)
					.create()
}