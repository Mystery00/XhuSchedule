package com.weilylab.xhuschedule.newPackage.repository.remote

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.constant.StringConstant
import com.weilylab.xhuschedule.newPackage.listener.DoSaveListener
import com.weilylab.xhuschedule.newPackage.listener.RequestListener
import com.weilylab.xhuschedule.newPackage.model.Notice
import com.weilylab.xhuschedule.newPackage.repository.dataSource.NoticeDataSource
import com.weilylab.xhuschedule.newPackage.repository.local.NoticeLocalDataSource
import com.weilylab.xhuschedule.newPackage.utils.NetworkUtil
import com.weilylab.xhuschedule.newPackage.utils.NoticeUtil
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData

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