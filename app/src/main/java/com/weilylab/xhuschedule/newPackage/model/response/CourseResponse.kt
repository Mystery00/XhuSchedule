package com.weilylab.xhuschedule.newPackage.model.response

import com.weilylab.xhuschedule.newPackage.model.Course

class CourseResponse {
	lateinit var rt: String
	lateinit var msg: String
	lateinit var courses: List<Course>
}