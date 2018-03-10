/*
 * Created by Mystery0 on 18-3-9 下午8:30.
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
 * Last modified 18-3-9 下午8:30
 */

package com.weilylab.xhuschedule.util

import android.content.Context
import android.graphics.Color
import androidx.content.edit
import com.weilylab.xhuschedule.APP
import com.weilylab.xhuschedule.classes.baseClass.Course
import vip.mystery0.tools.utils.Mystery0ColorUtil

object ColorUtil {
	private val sharedPreference = APP.getContext().getSharedPreferences(Constants.SHARED_PREFERENCE_COLOR, Context.MODE_PRIVATE)

	fun getCourseColor(keyName: String): Int {
		var color = getColor(keyName)
		if (color == 0) {
			color = Mystery0ColorUtil.getRandomColorAsInt()
			saveColor(keyName, color)
		}
		return color
	}

	fun getColor(keyName: String): Int {
		return sharedPreference.getInt(keyName, 0)
	}

	fun saveColor(keyName: String, color: Int) {
		sharedPreference.edit {
			putInt(keyName, color)
		}
	}

	fun parseCouseTableColor(course: Course): Int {
		return parseCourseColorWithAlpha(course, 255)
	}

	fun parseCourseColorWithAlpha(course: Course, alpha: Int): Int {
		val color = StringBuilder()
		color.append('#')
		var opacityString = Integer.toHexString(alpha)
		if (opacityString.length < 2)
			opacityString = "0$opacityString"
		color.append(opacityString)
		color.append((course.color and 0xff0000) shr 16)
		color.append((course.color and 0x00ff00) shr 8)
		color.append(course.color and 0x0000ff)
		return Color.parseColor(color.toString())
	}
}