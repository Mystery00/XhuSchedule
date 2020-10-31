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

    @ColumnInfo(name = "key")
    var key: String? = null
}