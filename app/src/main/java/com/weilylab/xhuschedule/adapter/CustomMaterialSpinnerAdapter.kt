/*
 * Created by Mystery0 on 18-2-28 上午12:52.
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
 * Last modified 18-2-28 上午12:39
 */

package com.weilylab.xhuschedule.adapter

import android.content.Context
import com.jaredrummler.materialspinner.MaterialSpinnerAdapter


class CustomMaterialSpinnerAdapter<T>(context: Context?, items: MutableList<T>?) : MaterialSpinnerAdapter<T>(context, items) {
	private var displayingHint = true

	override fun getCount(): Int {
		return items.size - if (displayingHint) 1 else 2
	}

	override fun notifyItemSelected(index: Int) {
		super.notifyItemSelected(index)
		displayingHint = index == items.size - 1
	}

	override fun get(position: Int): T {
		return when {
			position > items.size - 1 -> return items[items.size - 1]
			position < 0 -> items[0]
			else -> items[position]
		}
	}
}