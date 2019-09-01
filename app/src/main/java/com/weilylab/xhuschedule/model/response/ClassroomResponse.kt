package com.weilylab.xhuschedule.model.response

import com.weilylab.xhuschedule.model.Classroom

class ClassroomResponse : BaseResponse() {
	lateinit var classrooms: List<Classroom>
}