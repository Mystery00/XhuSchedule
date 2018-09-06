package com.weilylab.xhuschedule.service.widget

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.constant.IntentConstant
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.repository.WidgetRepository
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.viewModel.WidgetViewModelHelper
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.Status.*

class WidgetUpdateService : Service() {
	override fun onBind(intent: Intent): IBinder? = null

	private val studentListObserver = Observer<PackageData<List<Student>>> {
		when (it?.status) {
			Content -> {
				if (ConfigurationUtil.isEnableMultiUserMode) {
					WidgetRepository.queryTodayCourseForManyStudent()
					WidgetRepository.queryTestsForManyStudent()
				} else {
					WidgetRepository.queryTodayCourse()
					WidgetRepository.queryTests()
				}
			}
			Loading -> finishAndNotify(IntentConstant.INTENT_VALUE_WIDGET_ALL)
			Empty, Error -> finishAndNotify(IntentConstant.INTENT_VALUE_WIDGET_ALL, true)
		}
	}

	private val todayCourseObserver = Observer<PackageData<List<Schedule>>> {
		when (it?.status) {
			Loading -> finishAndNotify(IntentConstant.INTENT_VALUE_WIDGET_TODAY)
			Content, Empty, Error -> finishAndNotify(IntentConstant.INTENT_VALUE_WIDGET_TODAY, true)
		}
	}

	private val testObserver = Observer<PackageData<List<Test>>> {
		when (it?.status) {
			Loading -> finishAndNotify(IntentConstant.INTENT_VALUE_WIDGET_TEST)
			Content, Empty, Error -> finishAndNotify(IntentConstant.INTENT_VALUE_WIDGET_TEST, true)
		}
	}

	override fun onCreate() {
		super.onCreate()
		val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_DEFAULT)
				.setSmallIcon(R.drawable.ic_stat_init)
				.setContentText(getString(R.string.hint_foreground_notification))
				.setAutoCancel(true)
				.setPriority(NotificationManagerCompat.IMPORTANCE_NONE)
				.build()
		startForeground(Constants.NOTIFICATION_ID_WIDGET_UPDATE, notification)
		initObserver()
		WidgetRepository.queryStudentList()
	}

	private fun initObserver() {
		WidgetViewModelHelper.studentList.observeForever(studentListObserver)
		WidgetViewModelHelper.todayCourseList.observeForever(todayCourseObserver)
		WidgetViewModelHelper.testList.observeForever(testObserver)
	}

	private fun removeObserver() {
		WidgetViewModelHelper.studentList.removeObserver(studentListObserver)
		WidgetViewModelHelper.todayCourseList.removeObserver(todayCourseObserver)
		WidgetViewModelHelper.testList.removeObserver(testObserver)
	}

	private fun finishAndNotify(tag: String, isFinish: Boolean = false) {
		sendBroadcast(Intent(Constants.ACTION_WIDGET_UPDATE_BROADCAST)
				.putExtra(IntentConstant.INTENT_TAG_NAME_WIDGET_TAG, tag))
		if (isFinish)
			stopSelf()
	}

	override fun onDestroy() {
		stopForeground(true)
		removeObserver()
		super.onDestroy()
	}
}
