package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.CustomThing
import vip.mystery0.rx.PackageData

class CustomThingViewModel : ViewModel() {
	val customThingList by lazy { MutableLiveData<PackageData<List<CustomThing>>>() }
	val syncCustomThing by lazy { MutableLiveData<PackageData<Boolean>>() }
}