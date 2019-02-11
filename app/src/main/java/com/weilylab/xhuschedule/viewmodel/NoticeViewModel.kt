package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Notice
import vip.mystery0.rxpackagedata.PackageData

class NoticeViewModel : ViewModel() {
	val noticeList by lazy { MutableLiveData<PackageData<List<Notice>>>() }
}