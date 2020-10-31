/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "tb_student_info")
class StudentInfo {
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