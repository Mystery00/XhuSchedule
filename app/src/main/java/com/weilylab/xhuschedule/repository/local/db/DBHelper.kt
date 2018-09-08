package com.weilylab.xhuschedule.repository.local.db

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration


object DBHelper {
	private const val DATABASE_NAME = "db_xhuschedule"
	lateinit var db: DB

	fun init(context: Context) {
		db = Room.databaseBuilder(context.applicationContext, DB::class.java, DATABASE_NAME)
				.addMigrations(MIGRATION_1_2)
				.build()
	}

	private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
		override fun migrate(database: SupportSQLiteDatabase) {
			database.execSQL("alter table tb_course rename to _temp_tb_course")
			database.execSQL("CREATE TABLE IF NOT EXISTS `tb_course` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `course_teacher` TEXT NOT NULL, `course_week` TEXT NOT NULL, `course_name` TEXT NOT NULL, `course_location` TEXT NOT NULL, `course_time` TEXT NOT NULL, `course_type` TEXT NOT NULL, `course_day` TEXT NOT NULL, `course_year` TEXT NOT NULL, `course_term` TEXT NOT NULL, `student_id` TEXT NOT NULL, `edit_type` INTEGER NOT NULL, `course_color` TEXT NOT NULL)")
			database.execSQL("insert into tb_course select *,' ' from _temp_tb_course")
			database.execSQL("drop table _temp_tb_course")
			database.execSQL("CREATE TABLE IF NOT EXISTS `tb_fb_token` (`id` INTEGER NOT NULL, `username` TEXT NOT NULL, `fb_token` TEXT NOT NULL, PRIMARY KEY(`id`))")
			database.execSQL("CREATE TABLE IF NOT EXISTS `tb_feedback_message` (`dataBaseId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `origin_id` INTEGER NOT NULL, `receiver` TEXT NOT NULL, `create_time` TEXT NOT NULL, `sender` TEXT NOT NULL, `content` TEXT NOT NULL, `platform` TEXT NOT NULL, `status` INTEGER NOT NULL)")
		}
	}
}