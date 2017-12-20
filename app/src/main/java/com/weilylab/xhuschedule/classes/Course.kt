/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-21 上午3:25
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

    override fun toString(): String {
        return "课程名称：$name\n" +
                "任课教师：$teacher\n" +
                "上课周数：$week\n" +
                "星期：$day\n" +
                "上课时间：$time\n" +
                "上课地点：$location"
    }
}