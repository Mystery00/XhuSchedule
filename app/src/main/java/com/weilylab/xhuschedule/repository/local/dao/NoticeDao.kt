package com.weilylab.xhuschedule.repository.local.dao

import androidx.room.*
import com.weilylab.xhuschedule.model.Notice

@Dao
interface NoticeDao {
	@Insert
	fun add(notice: Notice): Long

	@Delete
	fun remove(notice: Notice): Int

	@Query("select * from tb_notice")
	fun queryAllNotice(): List<Notice>

	@Query("select * from tb_notice where notice_is_read = 1 limit 1")
	fun queryAllReadNotice(): List<Notice>

	@Query("select * from tb_notice where notice_platform = :platform")
	fun queryNoticeByPlatform(platform: String): List<Notice>

	@Query("select * from tb_notice where notice_id = :id limit 1")
	fun queryNoticeById(id: Int): Notice?

	@Update
	fun update(notice: Notice)
}