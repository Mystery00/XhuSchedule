package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.repository.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.content
import vip.mystery0.rx.launch

class SettingsViewModel : ViewModel(), KoinComponent {
	private val studentRepository: StudentRepository by inject()

	//学生列表
	val studentList by lazy { MutableLiveData<PackageData<List<Student>>>() }

	//所有学生信息列表
	val studentInfoList by lazy { MutableLiveData<PackageData<List<StudentInfo>>>() }

	fun initStudentList() {
		launch(studentList) {
			studentList.content(studentRepository.queryAllStudentList())
		}
	}

	fun updateStudentList(updateList: List<Student>) {
		launch(studentList) {
			studentRepository.updateStudentList(updateList)
			initStudentList()
		}
	}

	fun deleteStudentList(deleteList: List<Student>) {
		launch(studentList) {
			studentRepository.deleteStudentList(deleteList)
			initStudentList()
		}
	}

	fun queryAllStudentInfoListAndThen(block: (ArrayList<StudentInfo>) -> Unit) {
		launch(studentInfoList) {
			if (studentList.value == null) {
				studentList.content(studentRepository.queryAllStudentList())
			}
			val infoList = ArrayList<StudentInfo>()
			studentList.value?.data?.forEach {
				val info = studentRepository.queryStudentInfo(it)
				infoList.add(info)
			}
			studentInfoList.content(infoList)
			withContext(Dispatchers.Main) {
				block(infoList)
			}
		}
	}
}