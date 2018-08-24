package com.weilylab.xhuschedule.service.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.viewModel.WidgetViewModelHelper
import com.zhuangfei.timetable.model.ScheduleColorPool

class TodayCourseWidgetService : RemoteViewsService() {
	override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory = ListRemoteViewFactory(this)

	private inner class ListRemoteViewFactory(private val context: Context) : RemoteViewsFactory {
		override fun onCreate() {
		}

		override fun getLoadingView(): RemoteViews? = null

		override fun getItemId(position: Int): Long = position.toLong()

		override fun onDataSetChanged() {
		}

		override fun hasStableIds(): Boolean = true

		override fun getViewAt(position: Int): RemoteViews {
			val course = WidgetViewModelHelper.todayCourseList.value!!.data!![position]
			val remotesView = RemoteViews(context.packageName, R.layout.item_widget_today)
			remotesView.setTextViewText(R.id.course_name_textView, course.name)
			remotesView.setTextViewText(R.id.course_teacher_textView, course.teacher)
			val startTimeArray = context.resources.getStringArray(R.array.start_time)
			val endTimeArray = context.resources.getStringArray(R.array.end_time)
			remotesView.setTextViewText(R.id.course_time_location_textView, "${startTimeArray[course.start - 1]}-${endTimeArray[course.start + course.step - 2]} at ${course.room}")
			remotesView.setInt(R.id.background, "setBackgroundColor", ScheduleColorPool(context).getColorAuto(course.colorRandom))
			return remotesView
		}

		override fun getCount(): Int = WidgetViewModelHelper.todayCourseList.value!!.data!!.size

		override fun getViewTypeCount(): Int = 1

		override fun onDestroy() {
		}
	}
}