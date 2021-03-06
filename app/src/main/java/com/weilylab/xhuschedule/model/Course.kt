/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.model

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.weilylab.xhuschedule.config.ColorPoolHelper
import com.weilylab.xhuschedule.constant.Constants
import com.zhuangfei.timetable.model.Schedule
import com.zhuangfei.timetable.model.ScheduleEnable
import vip.mystery0.tools.utils.md5
import java.util.*

@Entity(tableName = "tb_course")
class Course : ScheduleEnable {
    /**
     * teacher : 陈红红
     * week : 11-16
     * name : 软件质量保证与测试
     * location : 6C-221多媒体95座
     * time : 1-2
     * type : 0
     * day : 2
     */
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "course_teacher")
    lateinit var teacher: String

    @ColumnInfo(name = "course_week")
    lateinit var week: String

    @ColumnInfo(name = "course_name")
    lateinit var name: String

    @ColumnInfo(name = "course_location")
    lateinit var location: String

    @ColumnInfo(name = "course_time")
    lateinit var time: String

    @ColumnInfo(name = "course_type")
    lateinit var type: String

    @ColumnInfo(name = "course_day")
    lateinit var day: String

    @ColumnInfo(name = "course_color")
    lateinit var color: String

    @ColumnInfo(name = "course_year")
    lateinit var year: String

    @ColumnInfo(name = "course_term")
    lateinit var term: String

    @ColumnInfo(name = "student_id")
    lateinit var studentID: String

    @ColumnInfo(name = "edit_type")
    var editType = 0//事项类型，课程默认为0，自定义的课程为1

    override fun getSchedule(): Schedule {
        val schedule = Schedule()
        schedule.name = name
        schedule.room = location
        schedule.teacher = teacher
        if (editType == 1) {
            schedule.weekList = week.split(",").map { it.toInt() }
        } else {
            val weekArray = week.trim().split('-')
            val weekList = ArrayList<Int>()
            for (i in weekArray[0].toInt()..weekArray[1].toInt()) {
                when (type) {
                    Constants.COURSE_TYPE_ALL -> weekList.add(i)
                    Constants.COURSE_TYPE_SINGLE -> if (i % 2 == 1) weekList.add(i)
                    Constants.COURSE_TYPE_DOUBLE -> if (i % 2 == 0) weekList.add(i)
                }
            }
            schedule.weekList = weekList
        }
        val timeArray = time.trim().split('-')
        schedule.start = timeArray[0].toInt()
        schedule.step = timeArray[1].toInt() - timeArray[0].toInt() + 1
        schedule.day = day.toInt()
        if (::color.isInitialized && color != "")
            try {
                schedule.extras["colorInt"] = Color.parseColor(color)
            } catch (e: Exception) {
                val md5Int = schedule.name.md5().substring(0, 1).toInt(16)
                schedule.extras["colorInt"] = ColorPoolHelper.colorPool.getColorAuto(md5Int)
            }
        else {
            val md5Int = schedule.name.md5().substring(0, 1).toInt(16)
            schedule.extras["colorInt"] = ColorPoolHelper.colorPool.getColorAuto(md5Int)
        }
        return schedule
    }
}
