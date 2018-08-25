package com.weilylab.xhuschedule.service.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.config.ColorPoolHelper
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.viewModel.WidgetViewModelHelper
import kotlin.math.roundToInt

class TestListWidgetService : RemoteViewsService() {
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
			val test = WidgetViewModelHelper.testList.value!!.data!![position]
			val remotesView = RemoteViews(context.packageName, R.layout.item_widget_test)
			remotesView.setTextViewText(R.id.exam_name, test.name)
			remotesView.setTextViewText(R.id.exam_no, "座位号：${test.testno}")
			remotesView.setTextViewText(R.id.exam_location, "考试地点：${test.location}")
			remotesView.setTextViewText(R.id.exam_time, "考试时间：${test.date} ${test.time.split('-')[0]}")
			remotesView.setTextViewText(R.id.exam_days, CalendarUtil.getTestDateText(test))
			val color = ColorPoolHelper.colorPool.getColorAuto((Math.random() * ColorPoolHelper.colorPool.size()).roundToInt())
			remotesView.setTextColor(R.id.exam_name, color)
			remotesView.setTextColor(R.id.exam_no, color)
			return remotesView
		}

		override fun getCount(): Int {
			return if (WidgetViewModelHelper.testList.value == null || WidgetViewModelHelper.testList.value!!.data == null)
				0
			else
				WidgetViewModelHelper.testList.value!!.data!!.size
		}

		override fun getViewTypeCount(): Int = 1

		override fun onDestroy() {
		}
	}
}