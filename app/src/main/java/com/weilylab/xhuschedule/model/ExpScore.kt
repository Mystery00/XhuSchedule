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

@Entity(tableName = "tb_exp_score")
class ExpScore {
    /**
     * no : 21030404
     * score : 68
     * coursename : 大学物理实验B
     * name : 电桥的使用
     * credit : 0.2
     * exptype : 必修
     */
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "score_no")
    lateinit var no: String

    @ColumnInfo(name = "score")
    lateinit var score: String

    @ColumnInfo(name = "score_course_name")
    lateinit var coursename: String

    @ColumnInfo(name = "score_name")
    lateinit var name: String

    @ColumnInfo(name = "score_credit")
    lateinit var credit: String

    @ColumnInfo(name = "score_exp_type")
    lateinit var exptype: String

    @ColumnInfo(name = "score_year")
    lateinit var year: String

    @ColumnInfo(name = "score_term")
    lateinit var term: String

    @ColumnInfo(name = "student_id")
    lateinit var studentID: String
}
