package com.weilylab.xhuschedule.newPackage.model.response

import com.weilylab.xhuschedule.newPackage.model.Course

class CourseResponse : BaseResponse() {
	lateinit var courses: List<Course>
}