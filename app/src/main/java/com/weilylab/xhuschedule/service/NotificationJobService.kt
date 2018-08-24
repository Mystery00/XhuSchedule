package com.weilylab.xhuschedule.service

import android.app.job.JobParameters
import android.app.job.JobService
import com.weilylab.xhuschedule.config.Status.*
import com.weilylab.xhuschedule.repository.NotificationRepository
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.utils.ConfigurationUtil

class NotificationJobService : JobService() {
	override fun onStopJob(params: JobParameters?): Boolean = true

	override fun onStartJob(params: JobParameters?): Boolean {
//		if (ConfigurationUtil.isEnableMultiUserMode)
//			StudentLocalDataSource.queryAllStudentList {
//				when(it.status){
//					Content ->
//					Loading -> TODO()
//					Empty -> TODO()
//					Error -> TODO()
//				}
//			}
//		else
//			StudentLocalDataSource.queryMainStudent {
//				when(it.status){
//					Content -> NotificationRepository.queryTomorrowCourseByUsername(it.data!!,null,null){
//
//					}
//					Loading -> TODO()
//					Empty -> TODO()
//					Error -> TODO()
//				}
//			}
		return true
	}
}