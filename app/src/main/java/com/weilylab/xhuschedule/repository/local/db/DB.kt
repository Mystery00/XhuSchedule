package com.weilylab.xhuschedule.repository.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weilylab.xhuschedule.model.*
import com.weilylab.xhuschedule.repository.local.dao.*

@Database(entities = [Student::class, StudentInfo::class, Course::class, Test::class, Notice::class, ClassScore::class], version = 1)
abstract class DB : RoomDatabase() {
	abstract fun getStudentDao(): StudentDao
	abstract fun getCourseDao(): CourseDao
	abstract fun getTestDao(): TestDao
	abstract fun getNoticeDao(): NoticeDao
	abstract fun getScoreDao(): ScoreDao
}