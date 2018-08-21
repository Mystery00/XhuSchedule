package com.weilylab.xhuschedule.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.CetScore
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData

object QueryCetScoreViewModelHelper {
	var no = MutableLiveData<String>()
	var name = MutableLiveData<String>()
	val student = MutableLiveData<Student>()
	val studentList = MutableLiveData<PackageData<List<Student>>>()
	val studentInfoList = MediatorLiveData<PackageData<Map<Student, StudentInfo?>>>()
	var cetVCodeLiveData = MutableLiveData<PackageData<Bitmap>>()
	var cetScoreLiveData = MutableLiveData<PackageData<CetScore>>()
}