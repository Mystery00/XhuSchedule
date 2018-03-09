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
import java.util.*

object ColorUtil {
	private val sharedPreference = APP.getContext().getSharedPreferences(Constants.SHARED_PREFERENCE_COLOR, Context.MODE_PRIVATE)

	fun getCourseColor(keyName: String): Int {
		var color = getColor(keyName)
		if (color == 0) {
			color = getRandomColorAsInt()
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

	fun parseColor(color: Int, alpha: Int): Int {
		return Color.parseColor(parseColorAsString(color, alpha))
	}

	fun parseColorAsString(color: Int, alpha: Int = 255): String {
		val parseColor = StringBuilder()
		parseColor.append('#')
		var opacityString = Integer.toHexString(alpha)
		if (opacityString.length < 2)
			opacityString = "0$opacityString"
		parseColor.append(opacityString.toUpperCase())
		var red = Integer.toHexString(Color.red(color)).toUpperCase()
		var green = Integer.toHexString(Color.green(color)).toUpperCase()
		var blue = Integer.toHexString(Color.blue(color)).toUpperCase()
		red = if (red.length == 1) "0" + red else red
		green = if (green.length == 1) "0" + green else green
		blue = if (blue.length == 1) "0" + blue else blue
		parseColor.append(red)
				.append(green)
				.append(blue)
		return parseColor.toString()
	}

	fun getRandomColorAsInt(alpha: Int = 255): Int {
		val random = Random()
		//生成红色颜色代码
		val red = random.nextInt(180) + 40
		//生成绿色颜色代码
		val green = random.nextInt(100) + 90
		//生成蓝色颜色代码
		val blue = random.nextInt(120) + 120
		return Color.argb(alpha, red, green, blue)
	}

	fun getRandomColorAsString(alpha: Int = 255): String {
		val color = StringBuilder()
		color.append('#')
		var opacityString = Integer.toHexString(alpha)
		if (opacityString.length < 2)
			opacityString = "0$opacityString"
		color.append(opacityString.toUpperCase())
		val random = Random()
		var red: String = Integer.toHexString(random.nextInt(180) + 40).toUpperCase()
		var green: String = Integer.toHexString(random.nextInt(100) + 90).toUpperCase()
		var blue: String = Integer.toHexString(random.nextInt(120) + 120).toUpperCase()
		red = if (red.length == 1) "0" + red else red
		green = if (green.length == 1) "0" + green else green
		blue = if (blue.length == 1) "0" + blue else blue
		//生成十六进制颜色值
		color.append(red).append(green).append(blue)
		return color.toString()
	}
}