package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.FeedBackMessage
import com.weilylab.xhuschedule.model.Student
import vip.mystery0.rxpackagedata.PackageData

class FeedBackViewModel : ViewModel() {
	val mainStudent by lazy { MutableLiveData<PackageData<Student>>() }
	val feedBackToken by lazy { MutableLiveData<PackageData<String>>() }
	val feedBackMessageList by lazy { MutableLiveData<PackageData<List<FeedBackMessage>>>() }
	val maxId by lazy { MutableLiveData<Int>() }
}