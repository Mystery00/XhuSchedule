/*
 * Created by Mystery0 on 18-3-11 上午10:21.
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
 * Last modified 18-3-11 上午10:21
 */

package com.weilylab.xhuschedule.activity

import android.support.v7.widget.LinearLayoutManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.ThemeAdapter
import com.weilylab.xhuschedule.classes.baseClass.Theme

import kotlinx.android.synthetic.main.activity_theme.*
import kotlinx.android.synthetic.main.content_theme.*
import vip.mystery0.tools.logs.Logs

class ThemeActivity : XhuBaseActivity() {
	private val list = ArrayList<Theme>()
	private lateinit var adapter: ThemeAdapter

	override fun initView() {
		super.initView()
		setContentView(R.layout.activity_theme)
		setSupportActionBar(toolbar)
	}

	override fun initData() {
		super.initData()
		adapter = ThemeAdapter(this, list)
	}

	override fun requestData() {
		super.requestData()
	}

	override fun loadDataToView() {
		super.loadDataToView()
		recyclerView.layoutManager = LinearLayoutManager(this)
		recyclerView.adapter = adapter
	}

	override fun monitor() {
		super.monitor()
		adapter.setThemtListener { theme, _ ->
			Logs.i(TAG, "monitor: ${theme.name}")
		}
	}
}
