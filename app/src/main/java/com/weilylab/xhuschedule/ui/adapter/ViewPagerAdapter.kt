/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.adapter

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.weilylab.xhuschedule.base.BaseBottomNavigationFragment
import java.util.*

/**
 * Created by myste.
 */
class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

	private val fragmentList = ArrayList<BaseBottomNavigationFragment<*>>()

	fun addFragment(fragment: BaseBottomNavigationFragment<*>) = fragmentList.add(fragment)

	override fun getItem(position: Int) = fragmentList[position]

	override fun getCount(): Int = fragmentList.size
}