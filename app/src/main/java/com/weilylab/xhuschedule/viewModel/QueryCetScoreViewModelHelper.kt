package com.weilylab.xhuschedule.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.CetScore
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import vip.mystery0.rxpackagedata.PackageData

object QueryCetScoreViewModelHelper {
	var no = MutableLiveData<String>()
	var name = MutableLiveData<String>()
	val student by lazy { MutableLiveData<Student>() }
	val studentList by lazy { MutableLiveData<PackageData<List<Student>>>() }
	val studentInfoList by lazy { MediatorLiveData<PackageData<Map<Student, StudentInfo?>>>() }
	var cetVCodeLiveData = MutableLiveData<PackageData<Bitmap>>()
	var cetScoreLiveData = MutableLiveData<PackageData<CetScore>>()
}