/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-27 上午3:50
 */

package com.weilylab.xhuschedule.adapter

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import java.util.ArrayList
import android.support.v4.app.Fragment
import vip.mystery0.tools.logs.Logs

/**
 * Created by myste.
 */
class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    companion object {
        private val TAG = "ViewPagerAdapter"
    }

    private val fragmentList = ArrayList<Fragment>()

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }

    override fun getItem(position: Int) = fragmentList[position]

    override fun getCount(): Int = fragmentList.size
}