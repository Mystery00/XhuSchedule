package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.repository.CourseRepository
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.content
import vip.mystery0.rx.launch

class ClassCourseColorViewModel : ViewModel(), KoinComponent {
	private val courseRepository: CourseRepository by inject()

	val classCourseList by lazy { MutableLiveData<PackageData<List<Course>>>() }

	fun queryDistinctCourseByUsernameAndTerm() {
		launch(classCourseList) {
			classCourseList.content(courseRepository.queryDistinctCourseByUsernameAndTerm())
		}
	}

	fun updateCourseColor(course: Course, color: String) {
		viewModelScope.launch {
			courseRepository.updateCourseColor(course, color)
		}
	}
}