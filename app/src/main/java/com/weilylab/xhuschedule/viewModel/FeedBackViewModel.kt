package com.weilylab.xhuschedule.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.FeedBackMessage
import com.weilylab.xhuschedule.model.Student
import vip.mystery0.rxpackagedata.PackageData

class FeedBackViewModel : ViewModel() {
	val mainStudent = MutableLiveData<PackageData<Student>>()
	val feedBackToken = MutableLiveData<PackageData<String>>()
	val feedBackMessageList = MutableLiveData<PackageData<List<FeedBackMessage>>>()
	val maxId = MutableLiveData<Int>()
}