package com.weilylab.xhuschedule.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_class_score")
class ClassScore {
	/**
	 * coursetype : 公共教育核心课
	 * no : 150588079
	 * score : 76
	 * name : 优生优育
	 * gpa : 1.00
	 * credit : 1.0
	 */
	@PrimaryKey(autoGenerate = true)
	var id = 0

	@ColumnInfo(name = "score_course_type")
	lateinit var coursetype: String
	@ColumnInfo(name = "score_no")
	lateinit var no: String
	@ColumnInfo(name = "score")
	lateinit var score: String
	@ColumnInfo(name = "score_name")
	lateinit var name: String
	@ColumnInfo(name = "score_gpa")
	lateinit var gpa: String
	@ColumnInfo(name = "score_credit")
	lateinit var credit: String
	@ColumnInfo(name = "score_failed")
	var failed = false
	@ColumnInfo(name = "score_year")
	lateinit var year: String
	@ColumnInfo(name = "score_term")
	lateinit var term: String
	@ColumnInfo(name = "student_id")
	lateinit var studentID: String
}
