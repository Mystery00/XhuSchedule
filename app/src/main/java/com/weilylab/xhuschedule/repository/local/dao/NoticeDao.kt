package com.weilylab.xhuschedule.repository.local.dao

import androidx.room.*
import com.weilylab.xhuschedule.model.Notice

@Dao
interface NoticeDao {
	@Insert
	suspend fun add(notice: Notice): Long

	@Delete
	suspend fun remove(notice: Notice): Int

	@Query("select * from tb_notice")
	suspend fun queryAllNotice(): List<Notice>

	@Query("select * from tb_notice where notice_is_read = 1 limit 1")
	suspend fun queryAllReadNotice(): List<Notice>

	@Query("select * from tb_notice where notice_platform = :platform")
	suspend fun queryNoticeByPlatform(platform: String): List<Notice>

	@Query("select * from tb_notice where notice_id = :id limit 1")
	suspend fun queryNoticeById(id: Int): Notice?

	@Update
	suspend fun update(notice: Notice)
}