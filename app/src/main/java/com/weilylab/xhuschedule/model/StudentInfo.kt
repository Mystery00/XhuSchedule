package com.weilylab.xhuschedule.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "tb_student_info")
class StudentInfo {
	/**
	 * profession : 软件工程
	 * msg : 成功
	 * no : 312015xxxxxxx
	 * rt : 0
	 * classname : 软件设计15-1
	 * grade : 2015
	 * sex : 男
	 * name : 邓易林
	 * institute : 计算机与软件工程学院
	 * direction : 软件设计技术
	 */
	@PrimaryKey(autoGenerate = true)
	var id = 0

	@ColumnInfo(name = "student_profession")
	lateinit var profession: String
	@ColumnInfo(name = "student_no")
	lateinit var no: String
	@ColumnInfo(name = "student_class")
	lateinit var classname: String
	@ColumnInfo(name = "student_grade")
	lateinit var grade: String
	@ColumnInfo(name = "student_sex")
	lateinit var sex: String
	@ColumnInfo(name = "student_name")
	lateinit var name: String
	@ColumnInfo(name = "student_institute")
	lateinit var institute: String
	@ColumnInfo(name = "student_direction")
	lateinit var direction: String
	@ColumnInfo(name = "student_id")
	lateinit var studentID: String

	@Ignore
	lateinit var msg: String
	@Ignore
	lateinit var rt: String
}