package com.weilylab.xhuschedule.config

import com.weilylab.xhuschedule.BR

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.zhuangfei.timetable.model.Schedule
import java.util.ArrayList

class TodayCourseAdapterHelper : BaseObservable() {
	@get:Bindable
	var todayCourseList = ArrayList<Schedule>()
		set(todayCourseList) {
			field = todayCourseList
			notifyPropertyChanged(BR.todayCourseList)
		}
}