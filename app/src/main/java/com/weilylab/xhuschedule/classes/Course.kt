package com.weilylab.xhuschedule.classes

import java.io.Serializable

/**
 * Created by myste.
 */
data class Course(var week: String, var teacher: String, var name: String, var location: String,
				  var time: String, var type: String, var day: String):Serializable
{
	var other: Course? = null

	fun with(course: Course)
	{
		if (other == null)
			other = course
	}
}