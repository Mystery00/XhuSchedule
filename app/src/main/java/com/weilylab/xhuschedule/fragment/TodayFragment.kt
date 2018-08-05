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

package com.weilylab.xhuschedule.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.adapter.TodayAdapter
import com.weilylab.xhuschedule.classes.baseClass.Course
import com.weilylab.xhuschedule.newPackage.base.BaseBottomNavigationFragment
import com.weilylab.xhuschedule.util.Constants
import com.weilylab.xhuschedule.util.ViewUtil
import java.util.*

/**
 * Created by myste.
 */
class TodayFragment : BaseBottomNavigationFragment(R.layout.fragment_todayold) {
	override fun updateTitle() {
	}

	companion object {

		fun newInstance(list: ArrayList<Course>): TodayFragment {
			val bundle = Bundle()
			bundle.putSerializable(Constants.INTENT_TAG_NAME_LIST, list)
			val fragment = TodayFragment()
			fragment.arguments = bundle
			return fragment
		}
	}

	private lateinit var list: ArrayList<Course>
	private lateinit var adapter: TodayAdapter
	private lateinit var backgroundImageView: ImageView
	private lateinit var recyclerView: RecyclerView
	private var isReady = false

	override fun initView() {
		isReady = true
		backgroundImageView = findViewById(R.id.background)
		recyclerView = findViewById(R.id.recycler_view)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		@Suppress("UNCHECKED_CAST")
		list = arguments?.getSerializable(Constants.INTENT_TAG_NAME_LIST) as ArrayList<Course>
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		setBackground()
		adapter = TodayAdapter(context!!, list)
		recyclerView.layoutManager = LinearLayoutManager(context)
		recyclerView.adapter = adapter
	}

	fun isReady(): Boolean {
		return isReady
	}

	fun setBackground() {
		setBackground(0)
	}

	/**
	 * 使用重试机制，每次延时400，重试5次
	 * @param time 当前重试的次数
	 */
	private fun setBackground(time: Int) {
		try {
			ViewUtil.setBackground(context!!, backgroundImageView)
		} catch (e: Exception) {
			if (time > 5)
				e.printStackTrace()
			else {
				Timer().schedule(object : TimerTask() {
					override fun run() {
						setBackground(time + 1)
					}
				}, 400)
			}
		}
	}

	fun refreshData() {
		adapter.notifyDataSetChanged()
	}
}