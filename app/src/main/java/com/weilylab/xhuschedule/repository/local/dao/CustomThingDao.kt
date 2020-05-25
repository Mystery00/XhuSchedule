package com.weilylab.xhuschedule.repository.local.dao

import androidx.room.*
import com.weilylab.xhuschedule.model.CustomThing

@Dao
interface CustomThingDao {
	@Insert
	suspend fun addThing(thing: CustomThing): Long

	@Delete
	suspend fun deleteThing(thing: CustomThing): Int

	@Update
	suspend fun updateThing(thing: CustomThing)

	@Query("select * from tb_custom_business")
	suspend fun queryAllThings(): List<CustomThing>
}