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
	var color = ""
	var transparencyColor = ""

	fun with(course: Course)
	{
		if (course.name == name)
		{
			location = "$location ($week)\n${course.location} (${course.week})"
			return
		}
		if (other == null)
			other = course
	}

	fun clone(): Course
	{
		val course = Course()
		course.week = week
		course.teacher = teacher
		course.name = name
		course.location = location
		course.time = time
		course.type = type
		course.day = day
		course.other = other
		course.color = color
		course.transparencyColor = transparencyColor
		return course
	}
}