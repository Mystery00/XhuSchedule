package com.weilylab.xhuschedule.newPackage.repository.local.db

import android.content.Context
import androidx.room.Room

object DBHelper {
	private const val DATABASE_NAME = "db_xhuschedule"
	lateinit var db: DB

	fun init(context: Context) {
		db = Room.databaseBuilder(context.applicationContext, DB::class.java, DATABASE_NAME)
				.build()
	}
}