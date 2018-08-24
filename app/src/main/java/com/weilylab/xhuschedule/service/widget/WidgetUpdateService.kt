package com.weilylab.xhuschedule.service.widget

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.config.Status
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.WidgetRepository
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.viewModel.WidgetViewModelHelper
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.logs.Logs

class WidgetUpdateService : Service() {
	override fun onBind(intent: Intent): IBinder? = null

	private val studentListObserver = Observer<PackageData<List<Student>>> {
		when (it?.status) {
			Status.Content -> {
				if (ConfigurationUtil.isEnableMultiUserMode)
					WidgetRepository.queryTodayCourseForManyStudent()
				else
					WidgetRepository.queryTodayCourse()
			}
			else -> sendBroadcast(Intent(Constants.ACTION_WIDGET_UPDATE_BROADCAST))
		}
	}

	private val todayCourseObserver = Observer<PackageData<List<Schedule>>> {
		sendBroadcast(Intent(Constants.ACTION_WIDGET_UPDATE_BROADCAST))
	}

	override fun onCreate() {
		super.onCreate()
		val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_DEFAULT)
				.setSmallIcon(R.drawable.ic_stat_init)
				.setContentText(getString(R.string.hint_foreground_notification))
				.setAutoCancel(true)
				.setPriority(NotificationManagerCompat.IMPORTANCE_NONE)
				.build()
		startForeground(20, notification)
		initObserver()
	}

	private fun initObserver() {
		WidgetViewModelHelper.studentList.observeForever(studentListObserver)
		WidgetViewModelHelper.todayCourseList.observeForever(todayCourseObserver)
	}

	private fun removeObserver() {
		WidgetViewModelHelper.studentList.removeObserver(studentListObserver)
		WidgetViewModelHelper.todayCourseList.removeObserver(todayCourseObserver)
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		WidgetRepository.queryStudentList()
		return super.onStartCommand(intent, flags, startId)
	}

	override fun onDestroy() {
		removeObserver()
		stopForeground(true)
		WidgetViewModelHelper.studentList.value = null
		WidgetViewModelHelper.todayCourseList.value = null
		super.onDestroy()
	}
}
