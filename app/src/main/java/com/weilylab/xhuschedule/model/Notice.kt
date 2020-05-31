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

@Entity(tableName = "tb_notice")
class Notice {
	@PrimaryKey()
	@ColumnInfo(name = "notice_id")
	var id = 0

	@ColumnInfo(name = "notice_create_time")
	var createTime = ""

	@ColumnInfo(name = "notice_title")
	var title = ""

	@ColumnInfo(name = "notice_content")
	var content = ""

	@ColumnInfo(name = "notice_platform")
	var platform = ""

	@ColumnInfo(name = "notice_is_read")
	var isRead: Boolean = false
}