package com.weilylab.xhuschedule.newPackage.repository.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weilylab.xhuschedule.newPackage.model.Course
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.StudentInfo
import com.weilylab.xhuschedule.newPackage.model.Test
import com.weilylab.xhuschedule.newPackage.repository.local.dao.CourseDao
import com.weilylab.xhuschedule.newPackage.repository.local.dao.StudentDao
import com.weilylab.xhuschedule.newPackage.repository.local.dao.TestDao

@Database(entities = [Student::class, StudentInfo::class, Course::class, Test::class], version = 1)
abstract class DB : RoomDatabase() {
	abstract fun getStudentDao(): StudentDao
	abstract fun getCourseDao(): CourseDao
	abstract fun getTestDao(): TestDao
}