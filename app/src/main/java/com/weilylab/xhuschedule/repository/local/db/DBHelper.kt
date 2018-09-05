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
			database.execSQL("ALTER TABLE tb_course RENAME TO _temp_tb_course")
			database.execSQL("CREATE TABLE IF NOT EXISTS `tb_course` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `course_teacher` TEXT NOT NULL, `course_week` TEXT NOT NULL, `course_name` TEXT NOT NULL, `course_location` TEXT NOT NULL, `course_time` TEXT NOT NULL, `course_type` TEXT NOT NULL, `course_day` TEXT NOT NULL, `course_year` TEXT NOT NULL, `course_term` TEXT NOT NULL, `student_id` TEXT NOT NULL, `edit_type` INTEGER NOT NULL, `course_color` TEXT NOT NULL)")
			database.execSQL("INSERT INTO tb_course SELECT *,' ' FROM _temp_tb_course")
			database.execSQL("DROP TABLE _temp_tb_course")
		}
	}
}