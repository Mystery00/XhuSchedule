/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

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
import vip.mystery0.rx.empty
import vip.mystery0.rx.launch

class ClassCourseColorViewModel : ViewModel(), KoinComponent {
	private val courseRepository: CourseRepository by inject()

	val classCourseList by lazy { MutableLiveData<PackageData<List<Course>>>() }

	fun queryDistinctCourseByUsernameAndTerm() {
		launch(classCourseList) {
			val list = courseRepository.queryDistinctCourseByUsernameAndTerm()
			if (list.isNullOrEmpty()) {
				classCourseList.empty()
			} else {
				classCourseList.content(list)
			}
		}
	}

	fun updateCourseColor(course: Course, color: String) {
		viewModelScope.launch {
			courseRepository.updateCourseColor(course, color)
		}
	}
}