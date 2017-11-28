/*
 * Created by Mystery0 on 17-11-28 下午8:03.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-28 下午8:03
 */

package com.weilylab.xhuschedule.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Profile

/**
 * Created by myste.
 */
class ProfileFragment : Fragment() {
    companion object {
        fun newInstance(profile: Profile): ProfileFragment {
            val bundle = Bundle()
            bundle.putSerializable("profile", profile)
            val fragment = ProfileFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var profile: Profile
    private var rootView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNCHECKED_CAST")
        profile = arguments.getSerializable("profile") as Profile
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        }
        return rootView
    }
}