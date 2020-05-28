package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.CustomThing
import com.weilylab.xhuschedule.repository.CustomThingRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.content
import vip.mystery0.rx.launch

class CustomThingViewModel : ViewModel(), KoinComponent {
	private val customThingRepository: CustomThingRepository by inject()

	val customThingList by lazy { MutableLiveData<PackageData<List<CustomThing>>>() }

	fun getAllCustomThing() {
		launch(customThingList) {
			getAllCustomThingInCoroutine()
		}
	}

	private suspend fun getAllCustomThingInCoroutine() {
		customThingList.content(customThingRepository.getAll())
	}

	fun saveCustomThing(thing: CustomThing, block: () -> Unit) {
		launch(customThingList) {
			customThingRepository.save(thing)
			block()
		}
	}

	fun updateCustomThing(thing: CustomThing, block: () -> Unit) {
		launch(customThingList) {
			customThingRepository.update(thing)
			block()
		}
	}

	fun deleteCustomThing(thing: CustomThing, block: () -> Unit) {
		launch(customThingList) {
			customThingRepository.delete(thing)
			block()
		}
	}

	fun syncForLocal() {
		launch(customThingList) {
			customThingList.content(customThingRepository.syncCustomThingForLocal())
		}
	}

	fun syncForRemote() {
		launch(customThingList) {
			customThingList.content(customThingRepository.syncCustomThingForServer())
		}
	}
}