package com.weilylab.xhuschedule.newPackage.repository.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weilylab.xhuschedule.newPackage.model.Course
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.StudentInfo
import com.weilylab.xhuschedule.newPackage.repository.local.dao.CourseDao
import com.weilylab.xhuschedule.newPackage.repository.local.dao.StudentDao

@Database(entities = [Student::class, StudentInfo::class, Course::class], version = 1)
abstract class DB : RoomDatabase() {
	abstract fun getStudentDao(): StudentDao
	abstract fun getCourseDao(): CourseDao
}