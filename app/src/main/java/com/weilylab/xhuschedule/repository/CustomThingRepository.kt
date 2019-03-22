package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.model.CustomThing
import com.weilylab.xhuschedule.repository.local.CustomThingLocalDataSource
import com.weilylab.xhuschedule.repository.remote.CustomThingRemoteDataSource
import com.weilylab.xhuschedule.viewmodel.BottomNavigationViewModel
import com.weilylab.xhuschedule.viewmodel.CustomThingViewModel

object CustomThingRepository {
	fun getToday(bottomNavigationViewModel: BottomNavigationViewModel) = CustomThingLocalDataSource.getToday(bottomNavigationViewModel.customThingList)

	fun getAll(customThingViewModel: CustomThingViewModel) = CustomThingLocalDataSource.getAll(customThingViewModel.customThingList)

	fun save(thing: CustomThing, listener: (Boolean, Throwable?) -> Unit) = CustomThingLocalDataSource.save(thing, listener)

	fun update(thing: CustomThing, listener: (Boolean, Throwable?) -> Unit) = CustomThingLocalDataSource.update(thing, listener)

	fun delete(thing: CustomThing, listener: (Boolean) -> Unit) = CustomThingLocalDataSource.delete(thing, listener)

	fun syncCustomThingForLocal(customCourseViewModel: CustomThingViewModel) = CustomThingRemoteDataSource.syncCustomThingForLocal(customCourseViewModel.syncCustomThing, "customThing")

	fun syncCustomThingForServer(customCourseViewModel: CustomThingViewModel) = CustomThingRemoteDataSource.syncCustomThingForServer(customCourseViewModel.syncCustomThing, "customThing")
}