package com.weilylab.xhuschedule.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData

class QueryClassScoreViewModel : ViewModel() {
	val scoreList = MutableLiveData<PackageData<List<String>>>()
}