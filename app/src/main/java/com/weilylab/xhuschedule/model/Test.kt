package com.weilylab.xhuschedule.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_test")
class Test {
	/**
	 * date : 2018-06-02
	 * no : (2017-2018-2)-153199029-0120040042-2
	 * testno : 67
	 * sname : 邓易林
	 * name : 信息检索
	 * testtype :
	 * location : 6B-307多媒体286座
	 * time : 8:00-9:50
	 * region : 校本部
	 */
	@PrimaryKey(autoGenerate = true)
	var id = 0

	@ColumnInfo(name = "test_date")
	lateinit var date: String
	@ColumnInfo(name = "test_no")
	lateinit var no: String
	@ColumnInfo(name = "test_testno")
	lateinit var testno: String
	@ColumnInfo(name = "test_sname")
	lateinit var sname: String
	@ColumnInfo(name = "test_name")
	lateinit var name: String
	@ColumnInfo(name = "test_testtype")
	lateinit var testtype: String
	@ColumnInfo(name = "test_location")
	lateinit var location: String
	@ColumnInfo(name = "test_time")
	lateinit var time: String
	@ColumnInfo(name = "test_region")
	lateinit var region: String
	@ColumnInfo(name = "student_id")
	lateinit var studentID: String
}
