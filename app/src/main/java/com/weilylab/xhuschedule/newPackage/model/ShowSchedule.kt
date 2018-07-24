package com.weilylab.xhuschedule.newPackage.model

import com.zhuangfei.timetable.model.Schedule

class ShowSchedule : Schedule {
	constructor(name: String?, room: String?, teacher: String?, weekList: MutableList<Int>?, start: Int, step: Int, day: Int, colorRandom: Int, time: String?) : super(name, room, teacher, weekList, start, step, day, colorRandom, time)
	constructor(name: String?, room: String?, teacher: String?, weekList: MutableList<Int>?, start: Int, step: Int, day: Int, colorRandom: Int) : super(name, room, teacher, weekList, start, step, day, colorRandom)
	constructor() : super()
}