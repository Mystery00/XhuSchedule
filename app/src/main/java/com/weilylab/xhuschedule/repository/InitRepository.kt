package com.weilylab.xhuschedule.repository

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.repository.local.InitLocalDataSource
import com.weilylab.xhuschedule.repository.remote.InitRemoteDataSource
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import vip.mystery0.rx.PackageData
import java.util.*

object InitRepository {
	fun getStartTime(startDateTimeLiveData: MutableLiveData<PackageData<Calendar>>) {
		if (ConfigurationUtil.isCustomStartTime)
			InitLocalDataSource.getStartDateTime(startDateTimeLiveData)
		else
			InitRemoteDataSource.getStartDateTime(startDateTimeLiveData)
	}
}