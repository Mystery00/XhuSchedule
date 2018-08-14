package com.weilylab.xhuschedule.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Notice
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData

class NoticeViewModel : ViewModel() {
	val noticeList = MutableLiveData<PackageData<List<Notice>>>()
}