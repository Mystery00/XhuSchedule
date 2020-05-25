package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.model.CustomThing
import com.weilylab.xhuschedule.repository.local.CustomThingLocalDataSource
import com.weilylab.xhuschedule.repository.local.dao.CustomThingDao
import com.weilylab.xhuschedule.repository.remote.CustomThingRemoteDataSource
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.viewmodel.CustomThingViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject

class CustomThingRepository : KoinComponent {
	private val customThingDao: CustomThingDao by inject()

	suspend fun getToday() = customThingDao.queryAllThings().filter { c -> CalendarUtil.isThingOnDay(c) }

	fun getAll(customThingViewModel: CustomThingViewModel) = CustomThingLocalDataSource.getAll(customThingViewModel.customThingList)

	fun save(thing: CustomThing, listener: (Boolean, Throwable?) -> Unit) = CustomThingLocalDataSource.save(thing, listener)

	fun update(thing: CustomThing, listener: (Boolean, Throwable?) -> Unit) = CustomThingLocalDataSource.update(thing, listener)

	fun delete(thing: CustomThing, listener: (Boolean) -> Unit) = CustomThingLocalDataSource.delete(thing, listener)

	fun syncCustomThingForLocal(customCourseViewModel: CustomThingViewModel) = CustomThingRemoteDataSource.syncCustomThingForLocal(customCourseViewModel.syncCustomThing, "customThing")

	fun syncCustomThingForServer(customCourseViewModel: CustomThingViewModel) = CustomThingRemoteDataSource.syncCustomThingForServer(customCourseViewModel.syncCustomThing, "customThing")
}