package com.weilylab.xhuschedule.classes

import java.io.Serializable

/**
 * Created by myste.
 */
class Course : Serializable
{
	var week: String = ""
	var teacher: String = ""
	var name: String = ""
	var location: String = ""
	var time: String = ""
	var type: String = ""
	var day: String = ""
	var other: Course? = null

	fun with(course: Course)
	{
		if (other == null)
			other = course
	}
}