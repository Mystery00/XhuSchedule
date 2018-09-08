package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.repository.local.FeedBackLocalDataSource
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.repository.remote.FeedBackRemoteDataSource
import com.weilylab.xhuschedule.utils.NetworkUtil
import com.weilylab.xhuschedule.viewModel.FeedBackViewModel
import vip.mystery0.rxpackagedata.PackageData

object FeedBackRepository {
	fun sendMessage(content: String, feedBackViewModel: FeedBackViewModel) {
		if (feedBackViewModel.mainStudent.value?.data == null)
			feedBackViewModel.feedBackMessageList.value = PackageData.error(Exception(StringConstant.hint_feedback_null_student))
		else
			FeedBackRemoteDataSource.sendFeedBackMessage(feedBackViewModel.feedBackMessageList, feedBackViewModel.maxId, feedBackViewModel.mainStudent.value!!.data!!, content, feedBackViewModel.feedBackToken.value!!.data!!)
	}

	fun getMessageFromLocal(feedBackViewModel: FeedBackViewModel) {
		if (feedBackViewModel.mainStudent.value?.data == null)
			feedBackViewModel.feedBackMessageList.value = PackageData.error(Exception(StringConstant.hint_feedback_null_student))
		else
			FeedBackLocalDataSource.queryFeedBackForStudent(feedBackViewModel.feedBackMessageList, feedBackViewModel.maxId, feedBackViewModel.mainStudent.value!!.data!!)
	}

	fun getMessageFromServer(feedBackViewModel: FeedBackViewModel) {
		feedBackViewModel.feedBackMessageList.value = PackageData.loading()
		if (feedBackViewModel.mainStudent.value?.data == null)
			feedBackViewModel.feedBackMessageList.value = PackageData.error(Exception(StringConstant.hint_feedback_null_student))
		else
			FeedBackRemoteDataSource.queryFeedBackForStudent(feedBackViewModel.feedBackMessageList, feedBackViewModel.maxId, feedBackViewModel.mainStudent.value!!.data!!, feedBackViewModel.feedBackToken.value!!.data!!)
	}

	fun queryFeedBackToken(feedBackViewModel: FeedBackViewModel) {
		feedBackViewModel.feedBackToken.value = PackageData.loading()
		StudentLocalDataSource.queryFeedBackTokenForUsername(feedBackViewModel.mainStudent.value!!.data!!, feedBackViewModel.feedBackToken)
	}
}