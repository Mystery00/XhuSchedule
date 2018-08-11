package com.weilylab.xhuschedule.newPackage.config

import android.graphics.Color
import com.weilylab.xhuschedule.BR

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class DateAdapterHelper : BaseObservable() {
	@get:Bindable
	var monthString: String? = null
		set(monthString) {
			field = monthString
			notifyPropertyChanged(BR.monthString)
		}

	@get:Bindable
	var dayString = arrayOfNulls<String>(7)
		set(dayString) {
			field = dayString
			notifyPropertyChanged(BR.dayString)
		}

	@get:Bindable
	var colorArray = Array(8) { Color.WHITE }
		set(colorArray) {
			field = colorArray
			notifyPropertyChanged(BR.colorArray)
		}

	@get:Bindable
	var currentWeek = 1
		set(currentWeek) {
			field = currentWeek
			notifyPropertyChanged(BR.currentWeek)
		}
}
