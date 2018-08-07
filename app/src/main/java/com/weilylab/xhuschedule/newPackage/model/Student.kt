package com.weilylab.xhuschedule.newPackage.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_student")
class Student {
	@PrimaryKey(autoGenerate = true)
	var id = 0

	@ColumnInfo(name = "username")
	lateinit var username: String
	@ColumnInfo(name = "password")
	lateinit var password: String
	@ColumnInfo(name = "is_main")
	var isMain = false
	@ColumnInfo(name = "student_name")
	var studentName: String = ""
}