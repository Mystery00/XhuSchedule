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
import android.widget.TextView
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Profile

/**
 * Created by myste.
 */
class ProfileFragment : Fragment() {

    private var profile = Profile()
    private var rootView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_profile, container, false)
            setProfile(profile)
        }
        return rootView
    }

    fun setProfile(profile: Profile) {
        this.profile = profile
        if (rootView != null) {
            val number: TextView = rootView!!.findViewById(R.id.textView_student_number)
            val name: TextView = rootView!!.findViewById(R.id.textView_student_name)
            val sex: TextView = rootView!!.findViewById(R.id.textView_student_sex)
            val institute: TextView = rootView!!.findViewById(R.id.textView_student_institute)
            val professional: TextView = rootView!!.findViewById(R.id.textView_student_professional)
            val classname: TextView = rootView!!.findViewById(R.id.textView_student_classname)
            val grade: TextView = rootView!!.findViewById(R.id.textView_student_grade)
            val direction: TextView = rootView!!.findViewById(R.id.textView_student_direction)
            number.text = profile.no
            name.text = profile.name
            sex.text = profile.sex
            institute.text = profile.institute
            professional.text = profile.profession
            classname.text = profile.classname
            grade.text = profile.grade
            direction.text = profile.direction
        }
    }
}