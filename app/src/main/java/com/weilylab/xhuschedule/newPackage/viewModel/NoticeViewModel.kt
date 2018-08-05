package com.weilylab.xhuschedule.newPackage.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.newPackage.model.Notice
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData

class NoticeViewModel : ViewModel() {
	val noticeList = MutableLiveData<PackageData<List<Notice>>>()
}