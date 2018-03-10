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

package com.weilylab.xhuschedule.classes.baseClass

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
    var color = 0

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