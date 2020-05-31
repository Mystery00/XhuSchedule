/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.CustomThing
import com.weilylab.xhuschedule.repository.CustomThingRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.content
import vip.mystery0.rx.empty
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
		val list = customThingRepository.getAll()
		if (list.isNullOrEmpty()) {
			customThingList.empty()
		} else {
			customThingList.content(list)
		}
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
			val list = customThingRepository.syncCustomThingForLocal()
			if (list.isNullOrEmpty()) {
				customThingList.empty()
			} else {
				customThingList.content(list)
			}
		}
	}

	fun syncForRemote() {
		launch(customThingList) {
			val list = customThingRepository.syncCustomThingForServer()
			if (list.isNullOrEmpty()) {
				customThingList.empty()
			} else {
				customThingList.content(list)
			}
		}
	}
}