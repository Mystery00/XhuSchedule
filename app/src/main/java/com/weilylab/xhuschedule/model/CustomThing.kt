package com.weilylab.xhuschedule.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_custom_business")
class CustomThing {
	@PrimaryKey(autoGenerate = true)
	var id = 0

	@ColumnInfo(name = "title")
	lateinit var title: String

	@ColumnInfo(name = "start_time")
	lateinit var startTime: String

	@ColumnInfo(name = "endTime")
	lateinit var endTime: String

	@ColumnInfo(name = "is_all_day")
	var isAllDay: Boolean = false

	@ColumnInfo(name = "location")
	lateinit var location: String

	@ColumnInfo(name = "color")
	lateinit var color: String

	@ColumnInfo(name = "mark")
	lateinit var mark: String
}