package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.repository.local.CustomThingLocalDataSource
import com.weilylab.xhuschedule.viewmodel.BottomNavigationViewModel

object CustomThingRepository {
	fun get(bottomNavigationViewModel: BottomNavigationViewModel) = CustomThingLocalDataSource.get(bottomNavigationViewModel.customThingList)
}