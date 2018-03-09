/*
 * Created by Mystery0 on 18-2-21 下午9:12.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 18-2-21 下午9:11
 */

package com.weilylab.xhuschedule.util.widget

import android.content.Context
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.CalendarUtil
import com.weilylab.xhuschedule.util.CourseUtil

/**
 * Created by mystery0.
 */
class ExamListRemotesViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

	override fun onCreate() {
	}

	override fun getLoadingView(): RemoteViews? {
		return null
	}

	override fun getItemId(position: Int): Long {
		return position.toLong()
	}

	override fun onDataSetChanged() {
	}

	override fun hasStableIds(): Boolean {
		return true
	}

	override fun getViewAt(position: Int): RemoteViews {
		return if (WidgetHelper.showExamList.size != 0) {
			val exam = WidgetHelper.showExamList[position]
			val remotesView = RemoteViews(context.packageName, R.layout.item_widget_exam)
			remotesView.setTextViewText(R.id.exam_name, exam.name)
			remotesView.setTextViewText(R.id.exam_location_time, "${exam.time} at ${exam.location}")
			remotesView.setTextViewText(R.id.exam_no, "座位号：${exam.testno}")
			remotesView.setTextViewText(R.id.exam_days, CalendarUtil.getExamShowInfo(context, exam))
			remotesView.setInt(R.id.background, "setBackgroundColor", CourseUtil.getColor(exam.name))
			remotesView
		} else {
			RemoteViews(context.packageName, R.layout.layout_widget_no_exam)
		}
	}

	override fun getCount(): Int {
		return if (WidgetHelper.showExamList.size != 0) WidgetHelper.showExamList.size else 1
	}

	override fun getViewTypeCount(): Int {
		return 2
	}

	override fun onDestroy() {
	}
}