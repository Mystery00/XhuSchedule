package com.weilylab.xhuschedule.utils.userDo

object CourseUtil {
	fun splitWeekString(list: List<Int>): String {
		val stringBuilder = StringBuilder()
		list.forEachIndexed f@{ index, i ->
			when (index) {
				0 -> stringBuilder.append(i)
				list.size - 1 -> {
					if (list[index] - list[index - 1] == 1) stringBuilder.append("-").append(list[index])
					else stringBuilder.append(",").append(list[index])
				}
				else -> {
					if ((list[index] - list[index - 1] == 1) && (list[index + 1] - list[index] == 1))
						return@f
					if ((list[index] - list[index - 1] == 1) && (list[index + 1] - list[index] != 1))
						stringBuilder.append("-").append(list[index])
					if (list[index] - list[index - 1] != 1)
						stringBuilder.append(",").append(list[index])
				}
			}
		}
		return stringBuilder.toString()
	}
}