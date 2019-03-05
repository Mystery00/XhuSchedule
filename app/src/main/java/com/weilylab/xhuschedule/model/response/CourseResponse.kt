package com.weilylab.xhuschedule.model.response

import com.weilylab.xhuschedule.model.Course

class CourseResponse : BaseResponse() {
	lateinit var courses: ArrayList<Course>
}