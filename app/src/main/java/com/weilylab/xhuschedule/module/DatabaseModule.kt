package com.weilylab.xhuschedule.module

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.weilylab.xhuschedule.config.APP.Companion.context
import com.weilylab.xhuschedule.repository.local.db.DB
import com.weilylab.xhuschedule.repository.local.service.*
import com.weilylab.xhuschedule.repository.local.service.impl.*
import org.koin.dsl.module

private const val DATABASE_NAME = "db_xhuschedule"

private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
	override fun migrate(database: SupportSQLiteDatabase) {
		database.execSQL("alter table tb_course rename to _temp_tb_course")
		database.execSQL("CREATE TABLE IF NOT EXISTS `tb_course` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `course_teacher` TEXT NOT NULL, `course_week` TEXT NOT NULL, `course_name` TEXT NOT NULL, `course_location` TEXT NOT NULL, `course_time` TEXT NOT NULL, `course_type` TEXT NOT NULL, `course_day` TEXT NOT NULL, `course_color` TEXT NOT NULL, `course_year` TEXT NOT NULL, `course_term` TEXT NOT NULL, `student_id` TEXT NOT NULL, `edit_type` INTEGER NOT NULL)")
		database.execSQL("insert into tb_course select *,' ' from _temp_tb_course")
		database.execSQL("drop table _temp_tb_course")
		database.execSQL("CREATE TABLE IF NOT EXISTS `tb_fb_token` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `username` TEXT NOT NULL, `fb_token` TEXT NOT NULL)")
		database.execSQL("CREATE TABLE IF NOT EXISTS `tb_feedback_message` (`dataBaseId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `origin_id` INTEGER NOT NULL, `receiver` TEXT NOT NULL, `create_time` TEXT NOT NULL, `sender` TEXT NOT NULL, `content` TEXT NOT NULL, `platform` TEXT NOT NULL, `status` INTEGER NOT NULL)")
	}
}

private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
	override fun migrate(database: SupportSQLiteDatabase) {
		database.execSQL("CREATE TABLE IF NOT EXISTS `tb_custom_business` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `start_time` TEXT NOT NULL, `endTime` TEXT NOT NULL, `is_all_day` INTEGER NOT NULL, `location` TEXT NOT NULL, `color` TEXT NOT NULL, `mark` TEXT NOT NULL)")
	}
}

val databaseModule = module {
	single {
		Room.databaseBuilder(context.applicationContext, DB::class.java, DATABASE_NAME)
				.addMigrations(MIGRATION_1_2)
				.addMigrations(MIGRATION_2_3)
				.build()
	}
	single { get<DB>().getStudentDao() }
	single { get<DB>().getCourseDao() }
	single { get<DB>().getTestDao() }
	single { get<DB>().getNoticeDao() }
	single { get<DB>().getScoreDao() }
	single { get<DB>().getFBTokenDao() }
	single { get<DB>().getFeedBackMessageDao() }
	single { get<DB>().getCustomThingDao() }
}