package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.StudentRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.content
import vip.mystery0.rx.launch
import vip.mystery0.rx.loading

class LoginViewModel : ViewModel(), KoinComponent {
	private val studentRepository: StudentRepository by inject()

	val loginLiveData by lazy { MutableLiveData<PackageData<Student>>() }

	fun login(student: Student) {
		loginLiveData.loading()
		launch(loginLiveData) {
			val logged = studentRepository.login(student)
			loginLiveData.content(logged)
		}
	}
}