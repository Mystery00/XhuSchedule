package com.weilylab.xhuschedule.service.widget

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.config.ColorPoolHelper
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.repository.WidgetRepository
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import kotlin.math.roundToInt

class TestListWidgetService : RemoteViewsService() {
	override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory = ListRemoteViewFactory(this)

	private inner class ListRemoteViewFactory(private val context: Context) : RemoteViewsFactory {
		private val data by lazy { ArrayList<Test>() }
		private var colorArray: IntArray = IntArray(0)

		override fun onCreate() {
		}

		override fun getLoadingView(): RemoteViews? = null

		override fun getItemId(position: Int): Long = position.toLong()

		override fun onDataSetChanged() {
			data.clear()
			if (ConfigurationUtil.isEnableMultiUserMode)
				data.addAll(WidgetRepository.queryTestsForManyStudent())
			else
				data.addAll(WidgetRepository.queryTests())
			if (data.isEmpty())
				sendBroadcast(Intent(Constants.ACTION_WIDGET_UPDATE_BROADCAST)
						.putExtra("name", TestListWidgetService::class.java.name)
						.putExtra("hasData", false))
			else
				colorArray = WidgetRepository.generateColorList(data)
		}

		override fun hasStableIds(): Boolean = true

		override fun getViewAt(position: Int): RemoteViews {
			val test = data[position]
			val remotesView = RemoteViews(context.packageName, R.layout.item_widget_test)
			remotesView.setTextViewText(R.id.exam_name, test.name)
			remotesView.setTextViewText(R.id.exam_no, "座位号：${test.testno}")
			remotesView.setTextViewText(R.id.exam_location, "考试地点：${test.location}")
			remotesView.setTextViewText(R.id.exam_time, "考试时间：${test.date} ${test.time.split('-')[0]}")
			remotesView.setTextViewText(R.id.exam_student, test.sname)
			remotesView.setTextViewText(R.id.exam_days, CalendarUtil.getTestDateText(test))
			remotesView.setTextColor(R.id.exam_name, colorArray[position])
			remotesView.setTextColor(R.id.exam_no, colorArray[position])
			if (!ConfigurationUtil.isEnableMultiUserMode)
				remotesView.setViewVisibility(R.id.exam_student, View.GONE)
			else
				remotesView.setViewVisibility(R.id.exam_student, View.VISIBLE)
			return remotesView
		}

		override fun getCount(): Int = data.size

		override fun getViewTypeCount(): Int = 1

		override fun onDestroy() {
			data.clear()
		}
	}
}