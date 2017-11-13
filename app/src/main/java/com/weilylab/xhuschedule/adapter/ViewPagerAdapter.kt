package com.weilylab.xhuschedule.adapter

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import java.util.ArrayList
import android.support.v4.app.Fragment

/**
 * Created by myste.
 */
class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager)
{

	private val fragmentList = ArrayList<Fragment>()

	fun addFragment(fragment: Fragment)
	{
//		if (fragmentList.contains(fragment))
//			return
		fragmentList.add(fragment)
	}

	override fun getItem(position: Int): Fragment
	{
		return fragmentList[position]
	}

	override fun getCount(): Int
	{
		return fragmentList.size
	}
}