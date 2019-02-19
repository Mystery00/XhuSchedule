package com.weilylab.xhuschedule.repository.local.service

import com.weilylab.xhuschedule.model.CustomThing

interface CustomThingService {
	fun addThing(thing: CustomThing): Long

	fun deleteThing(thing: CustomThing): Int

	fun updateThing(thing: CustomThing)

	fun queryAllThings(): List<CustomThing>
}