/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.repository.CustomCourseRepository
import com.weilylab.xhuschedule.repository.StudentRepository
import com.weilylab.xhuschedule.utils.CalendarUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.rx.*
import java.util.*
import kotlin.collections.HashMap

class CustomCourseViewModel : ViewModel(), KoinComponent {
    private val studentRepository: StudentRepository by inject()
    private val customCourseRepository: CustomCourseRepository by inject()

    val studentList by lazy { MutableLiveData<List<Student>>() }
    val studentInfoList by lazy { MutableLiveData<PackageData<Map<Student, StudentInfo?>>>() }
    val customCourseList by lazy { MutableLiveData<PackageData<List<Any>>>() }
    val time by lazy { MutableLiveData<Pair<Int, Int>>() }
    val weekIndex by lazy { MutableLiveData<Int>() }
    val mainStudent by lazy { MutableLiveData<Student>() }
    val year by lazy { MutableLiveData<String>() }
    val term by lazy { MutableLiveData<String>() }

    fun init() {
        customCourseList.loading()
        launch(customCourseList) {
            var studentArray = studentList.value
            if (studentArray.isNullOrEmpty()) {
                val list = studentRepository.queryAllStudentList()
                studentList.postValue(list)
                studentArray = list
            }
            var student: Student? = null
            val infoMap = HashMap<Student, StudentInfo?>()
            studentArray.forEach {
                try {
                    val info = studentRepository.queryStudentInfo(it, fromCache = true)
                    infoMap[it] = info
                    if (it.isMain) {
                        student = it
                        student?.studentName = info.name
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "init: ", e)
                }
            }
            if (infoMap.isNullOrEmpty()) {
                studentInfoList.empty()
            } else {
                studentInfoList.content(infoMap)
            }
            mainStudent.postValue(student)
            withContext(Dispatchers.Default) {
                year.postValue(CalendarUtil.getSelectArray(null).last())
                val now = Calendar.getInstance()
                now.firstDayOfWeek = Calendar.MONDAY
                val month = now.get(Calendar.MONTH)
                val week = now.get(Calendar.DAY_OF_WEEK)
                term.postValue(if (month in Calendar.MARCH until Calendar.SEPTEMBER) "2" else "1")
                weekIndex.postValue(week)
                time.postValue(Pair(1, 1))
            }
        }
    }

    fun getAllCustomCourse() {
        launch(customCourseList) {
            getAllCustomCourseInCoroutine()
        }
    }

    private suspend fun getAllCustomCourseInCoroutine() {
        val list = customCourseRepository.getAll()
        if (list.isNullOrEmpty()) {
            customCourseList.empty()
        } else {
            customCourseList.content(list)
        }
    }

    fun saveCustomCourse(course: Course, block: () -> Unit) {
        launch(customCourseList) {
            customCourseRepository.save(course)
            block()
        }
    }

    fun updateCustomCourse(course: Course, block: () -> Unit) {
        launch(customCourseList) {
            customCourseRepository.update(course)
            block()
        }
    }

    fun deleteCustomCourse(course: Course, block: () -> Unit) {
        launch(customCourseList) {
            customCourseRepository.delete(course)
            block()
        }
    }

    fun syncForLocal(student: Student) {
        launch(customCourseList) {
            customCourseRepository.syncCustomCourseForLocal(student)
            getAllCustomCourseInCoroutine()
        }
    }

    fun syncForRemote(student: Student) {
        launch(customCourseList) {
            customCourseRepository.syncCustomCourseForServer(student)
            getAllCustomCourseInCoroutine()
        }
    }

    companion object {
        private const val TAG = "CustomCourseViewModel"
    }
}