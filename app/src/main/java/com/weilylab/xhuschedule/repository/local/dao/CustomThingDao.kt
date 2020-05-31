/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

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