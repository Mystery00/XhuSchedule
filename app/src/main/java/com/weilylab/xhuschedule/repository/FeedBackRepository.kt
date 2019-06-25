package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.repository.local.FeedBackLocalDataSource
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.repository.remote.FeedBackRemoteDataSource
import com.weilylab.xhuschedule.utils.userDo.UserUtil
import com.weilylab.xhuschedule.viewmodel.BottomNavigationViewModel
import com.weilylab.xhuschedule.viewmodel.FeedBackViewModel
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.Status.*
import vip.mystery0.tools.utils.NetworkTools

object FeedBackRepository {
	fun sendMessage(content: String, feedBackViewModel: FeedBackViewModel) {
		if (feedBackViewModel.mainStudent.value?.data == null)
			feedBackViewModel.feedBackMessageList.value = PackageData.error(Exception(StringConstant.hint_null_student))
		else
			FeedBackRemoteDataSource.sendFeedBackMessage(feedBackViewModel.feedBackMessageList, feedBackViewModel.maxId, feedBackViewModel.mainStudent.value!!.data!!, content, feedBackViewModel.feedBackToken.value!!.data!!)
	}

	fun getMessageFromLocal(feedBackViewModel: FeedBackViewModel) {
		if (feedBackViewModel.mainStudent.value?.data == null)
			feedBackViewModel.feedBackMessageList.value = PackageData.error(Exception(StringConstant.hint_null_student))
		else
			FeedBackLocalDataSource.queryFeedBackForStudent(feedBackViewModel.feedBackMessageList, feedBackViewModel.maxId, feedBackViewModel.mainStudent.value!!.data!!)
	}

	fun getMessageFromServer(feedBackViewModel: FeedBackViewModel) {
		feedBackViewModel.feedBackMessageList.value = PackageData.loading()
		if (feedBackViewModel.mainStudent.value?.data == null)
			feedBackViewModel.feedBackMessageList.value = PackageData.error(Exception(StringConstant.hint_null_student))
		else
			FeedBackRemoteDataSource.queryFeedBackForStudent(feedBackViewModel.feedBackMessageList, feedBackViewModel.maxId, feedBackViewModel.mainStudent.value!!.data!!, feedBackViewModel.feedBackToken.value!!.data!!)
	}

	fun queryFeedBackToken(feedBackViewModel: FeedBackViewModel) {
		feedBackViewModel.feedBackToken.value = PackageData.loading()
		StudentLocalDataSource.queryFeedBackTokenForUsername(feedBackViewModel.mainStudent.value!!.data!!, feedBackViewModel.feedBackToken)
	}

	fun queryFeedBackMessageInMainActivity(bottomNavigationViewModel: BottomNavigationViewModel) {
		if (!NetworkTools.instance.isConnectInternet()) {
			bottomNavigationViewModel.newFeedBackMessageList.value = PackageData.empty()
			return
		}
		if (bottomNavigationViewModel.studentList.value?.data == null) {
			bottomNavigationViewModel.newFeedBackMessageList.value = PackageData.empty()
			return
		}
		if (bottomNavigationViewModel.feedBackToken.value == null) {
			val mainStudent = UserUtil.findMainStudent(bottomNavigationViewModel.studentList.value!!.data!!)
			if (mainStudent == null) {
				bottomNavigationViewModel.newFeedBackMessageList.value = PackageData.empty()
				return
			}
			StudentLocalDataSource.queryFeedBackTokenForUsername(mainStudent) { packageData ->
				when (packageData.status) {
					Content ->
						FeedBackLocalDataSource.queryMaxId(mainStudent.username) {
							FeedBackRemoteDataSource.onlyQueryFeedBackMessage(bottomNavigationViewModel.newFeedBackMessageList, mainStudent, packageData.data!!, it)
						}
					Empty -> bottomNavigationViewModel.newFeedBackMessageList.value = PackageData.empty()
					Error -> bottomNavigationViewModel.newFeedBackMessageList.value = PackageData.error(packageData.error)
					Loading -> {
					}
				}
			}
		}
	}
}