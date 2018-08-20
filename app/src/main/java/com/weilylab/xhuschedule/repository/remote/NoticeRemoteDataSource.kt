package com.weilylab.xhuschedule.repository.remote

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.listener.DoSaveListener
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.Notice
import com.weilylab.xhuschedule.repository.dataSource.NoticeDataSource
import com.weilylab.xhuschedule.repository.local.NoticeLocalDataSource
import com.weilylab.xhuschedule.utils.NetworkUtil
import com.weilylab.xhuschedule.utils.NoticeUtil
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData

object NoticeRemoteDataSource : NoticeDataSource {
	override fun queryNotice(noticeLiveData: MutableLiveData<PackageData<List<Notice>>>, platform: String?) {
		if (NetworkUtil.isConnectInternet()) {
			NoticeUtil.getNotice(platform, object : DoSaveListener<List<Notice>> {
				override fun doSave(t: List<Notice>) {
					NoticeLocalDataSource.saveNotice(t)
				}
			}, object : RequestListener<List<Notice>> {
				override fun done(t: List<Notice>) {
					noticeLiveData.value = PackageData.content(t)
				}

				override fun error(rt: String, msg: String?) {
					noticeLiveData.value = PackageData.error(Exception(msg))
					NoticeLocalDataSource.queryNotice(noticeLiveData, platform)
				}
			})
		} else {
			noticeLiveData.value = PackageData.error(Exception(StringConstant.hint_network_error))
			NoticeLocalDataSource.queryNotice(noticeLiveData, platform)
		}
	}
}