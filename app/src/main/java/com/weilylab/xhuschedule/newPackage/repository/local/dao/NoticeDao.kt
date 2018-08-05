package com.weilylab.xhuschedule.newPackage.repository.local.dao

import androidx.room.*
import com.weilylab.xhuschedule.newPackage.model.Notice

@Dao
interface NoticeDao {
	@Insert
	fun add(notice: Notice): Long

	@Delete
	fun remove(notice: Notice): Int

	@Query("select * from tb_notice")
	fun queryAllNotice(): List<Notice>

	@Query("select * from tb_notice where notice_platform = :platform")
	fun queryNoticeByPlatform(platform: String): List<Notice>

	@Query("select * from tb_notice where notice_id = :id limit 1")
	fun queryNoticeById(id: Int): Notice?

	@Update
	fun update(notice: Notice)
}