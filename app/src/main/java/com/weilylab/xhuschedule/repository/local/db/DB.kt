/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.repository.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weilylab.xhuschedule.model.*
import com.weilylab.xhuschedule.repository.local.dao.*

@Database(entities = [Student::class, StudentInfo::class, Course::class, Test::class, Notice::class, ClassScore::class, ExpScore::class, FeedBackToken::class, FeedBackMessage::class, CustomThing::class], version = 4)
abstract class DB : RoomDatabase() {
	abstract fun getStudentDao(): StudentDao
	abstract fun getCourseDao(): CourseDao
	abstract fun getTestDao(): TestDao
	abstract fun getNoticeDao(): NoticeDao
	abstract fun getScoreDao(): ScoreDao
	abstract fun getFBTokenDao(): FBTokenDao
	abstract fun getFeedBackMessageDao(): FeedBackMessageDao
	abstract fun getCustomThingDao(): CustomThingDao
}