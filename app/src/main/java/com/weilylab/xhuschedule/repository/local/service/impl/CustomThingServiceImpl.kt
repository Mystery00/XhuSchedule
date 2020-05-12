package com.weilylab.xhuschedule.repository.local.service.impl

import com.weilylab.xhuschedule.model.CustomThing
import com.weilylab.xhuschedule.repository.local.dao.CustomThingDao
import com.weilylab.xhuschedule.repository.local.service.CustomThingService

class CustomThingServiceImpl(private val customThingDao: CustomThingDao) : CustomThingService {

	override fun addThing(thing: CustomThing): Long = customThingDao.addThing(thing)

	override fun deleteThing(thing: CustomThing): Int = customThingDao.deleteThing(thing)

	override fun updateThing(thing: CustomThing) = customThingDao.updateThing(thing)

	override fun queryAllThings(): List<CustomThing> = customThingDao.queryAllThings()
}