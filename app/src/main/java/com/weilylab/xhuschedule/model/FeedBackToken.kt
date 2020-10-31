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

@Entity(tableName = "tb_fb_token")
class FeedBackToken {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "username")
    lateinit var username: String

    @ColumnInfo(name = "fb_token")
    lateinit var fbToken: String
}