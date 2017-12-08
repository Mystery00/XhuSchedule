/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-26 下午8:18
 */

package com.weilylab.xhuschedule.classes

import com.weilylab.xhuschedule.util.CourseUtil
import java.io.Serializable

/**
 * Created by myste.
 */
class Course : Serializable {
    var week: String = ""
    var teacher: String = ""
    var name: String = ""
    var location: String = ""
    var time: String = ""
    var type: String = ""
    var day: String = ""
    var color = ""

    fun with(course: Course): Boolean {
        if (course.name == name) {
            location = "$location ($week)\n${course.location} (${course.week})"
            type = CourseUtil.typeMerge(type, course.type)
            return true
        }
        return false
    }
}