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

package com.weilylab.xhuschedule.util.widget

import android.content.Context
import android.util.Base64
import com.weilylab.xhuschedule.classes.baseClass.Course
import com.weilylab.xhuschedule.classes.baseClass.Exam
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.util.CalendarUtil
import com.weilylab.xhuschedule.util.CourseUtil
import com.weilylab.xhuschedule.util.XhuFileUtil
import java.io.File

/**
 * Created by mystery0.
 */
object WidgetHelper {
    const val TODAY_TAG = "TODAY_TAG"
    const val TABLE_TAG = "TABLE_TAG"
    const val ALL_TAG = "ALL_TAG"
    const val EXAM_TAG = "EXAM_TAG"
    var dayIndex = 0
    val showTodayCourses = ArrayList<Course>()
    val showScheduleCourses = ArrayList<ArrayList<ArrayList<Course>>>()
    val showExamList = ArrayList<Exam>()

    /**
     * 同步当天时间
     */
    fun syncDayIndex() {
        dayIndex = CalendarUtil.getDay()
    }

    /**
     * 检测是否有数据
     */
    fun hasData(data: ArrayList<ArrayList<ArrayList<Course>>>): Boolean {
        var isEmpty = true
        for (a in 0 until data.size)
            for (b in 0 until data[a].size)
                if (data[a][b].isNotEmpty()) {
                    isEmpty = false
                    break
                }
        return !isEmpty
    }

    /**
     * 刷新内存中的当天课表
     */
    fun refreshTodayCourses(context: Context) {
        showTodayCourses.clear()
        val studentList = XhuFileUtil.getArrayFromFile(XhuFileUtil.getStudentListFile(context), Student::class.java)
        if (studentList.isEmpty())
            return
        var mainStudent: Student? = (0 until studentList.size)
                .firstOrNull { studentList[it].isMain }
                ?.let { studentList[it] }
        if (mainStudent == null)
            mainStudent = studentList[0]
        val parentFile = XhuFileUtil.getCourseCacheParentFile(context)
        if (!parentFile.exists())
            parentFile.mkdirs()
        val base64Name = XhuFileUtil.filterString(Base64.encodeToString(mainStudent.username.toByteArray(), Base64.DEFAULT))
        //判断是否有缓存
        val cacheResult = parentFile.listFiles().filter { it.name == base64Name }.size == 1
        if (!cacheResult) {
            return
        }
        val oldFile = File(parentFile, base64Name)
        if (!oldFile.exists()) {
            return
        }
        showTodayCourses.addAll(CourseUtil.getTodayCourses(XhuFileUtil.getCoursesFromFile(context, oldFile), dayIndex))
    }

    /**
     * 刷新内存中的本周课表
     */
    fun refreshWeekCourses(context: Context) {
        showScheduleCourses.clear()
        val studentList = XhuFileUtil.getArrayFromFile(XhuFileUtil.getStudentListFile(context), Student::class.java)
        if (studentList.isEmpty())
            return
        var mainStudent: Student? = (0 until studentList.size)
                .firstOrNull { studentList[it].isMain }
                ?.let { studentList[it] }
        if (mainStudent == null)
            mainStudent = studentList[0]
        val parentFile = XhuFileUtil.getCourseCacheParentFile(context)
        if (!parentFile.exists())
            parentFile.mkdirs()
        val base64Name = XhuFileUtil.filterString(Base64.encodeToString(mainStudent.username.toByteArray(), Base64.DEFAULT))
        //判断是否有缓存
        val cacheResult = parentFile.listFiles().filter { it.name == base64Name }.size == 1
        if (!cacheResult) {
            return
        }
        val oldFile = File(parentFile, base64Name)
        if (!oldFile.exists()) {
            return
        }
        showScheduleCourses.addAll(CourseUtil.getWeekCourses(XhuFileUtil.getCoursesFromFile(context, oldFile)))
    }

    /**
     * 刷新内存中的考试列表
     */
    fun refreshExamList(context: Context) {
        val studentList = XhuFileUtil.getArrayFromFile(File(context.filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java)
        if (studentList.isEmpty())
            return
        var mainStudent: Student? = (0 until studentList.size)
                .firstOrNull { studentList[it].isMain }
                ?.let { studentList[it] }
        if (mainStudent == null)
            mainStudent = studentList[0]
        val parentFile = XhuFileUtil.getExamParentFile(context)
        if (!parentFile.exists())
            parentFile.mkdirs()
        val base64Name = XhuFileUtil.filterString(Base64.encodeToString(mainStudent.username.toByteArray(), Base64.DEFAULT))
        //判断是否有缓存
        val cacheResult = parentFile.listFiles().filter { it.name == base64Name }.size == 1
        if (!cacheResult) {
            return
        }
        val oldFile = File(parentFile, base64Name)
        if (!oldFile.exists()) {
            return
        }
        getExamList(XhuFileUtil.getArrayListFromFile(oldFile, Exam::class.java))
    }

    /**
     * 解析转换考试列表
     */
    private fun getExamList(oldList: ArrayList<Exam>) {
        showExamList.clear()
        showExamList.addAll(oldList.filter { it.date != "" }
                .toList())
    }

    /**
     * 将小部件id集合保存起来
     */
    fun saveWidgetIds(context: Context, name: String, appWidgetIds: IntArray) {
        val sharedPreference = context.getSharedPreferences("ids", Context.MODE_PRIVATE)
        sharedPreference.edit().putStringSet(name, appWidgetIds.map { it.toString() }.toSet()).apply()
    }

    /**
     * 从文件中获取小部件id集合
     */
    fun getWidgetIds(context: Context, name: String): IntArray {
        val sharedPreference = context.getSharedPreferences("ids", Context.MODE_PRIVATE)
        return sharedPreference.getStringSet(name, HashSet<String>()).map { it.toInt() }.toIntArray()
    }
}