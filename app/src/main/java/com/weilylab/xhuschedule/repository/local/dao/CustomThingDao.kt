package com.weilylab.xhuschedule.repository.local.dao

import androidx.room.*
import com.weilylab.xhuschedule.model.CustomThing

@Dao
interface CustomThingDao {
	@Insert
	fun addThing(thing: CustomThing): Long

	@Delete
	fun deleteThing(thing: CustomThing): Int

	@Update
	fun updateThing(thing: CustomThing)

	@Query("select * from tb_custom_business")
	fun queryAllThings(): List<CustomThing>
}