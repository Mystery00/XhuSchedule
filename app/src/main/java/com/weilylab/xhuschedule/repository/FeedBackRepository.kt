package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.api.FeedbackAPI
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.model.FeedBackToken
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.module.redoAfterLogin
import com.weilylab.xhuschedule.repository.local.dao.FBTokenDao
import com.weilylab.xhuschedule.repository.local.dao.FeedBackMessageDao
import com.weilylab.xhuschedule.repository.remote.FeedBackRemoteDataSource
import com.weilylab.xhuschedule.viewmodel.FeedBackViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.rx.PackageData

class FeedBackRepository : KoinComponent {
	private val fbTokenDao: FBTokenDao by inject()
	private val feedBackMessageDao: FeedBackMessageDao by inject()

	private val feedBackApi: FeedbackAPI by inject()

	suspend fun registerFeedBackToken(student: Student, feedBackToken: String) {
		withContext(Dispatchers.IO) {
			var fbToken = fbTokenDao.queryFeedBackTokenForUsername(student.username)
			if (fbToken == null) {
				fbToken = FeedBackToken()
				fbToken.username = student.username
				fbToken.fbToken = feedBackToken
				fbTokenDao.register(fbToken)
			} else {
				fbToken.fbToken = feedBackToken
				fbTokenDao.register(fbToken)
			}
		}
	}

	suspend fun queryNewFeedback(mainStudent: Student): Boolean {
		val fbToken = fbTokenDao.queryFeedBackTokenForUsername(mainStudent.username) ?: return false
		val maxId = feedBackMessageDao.queryMaxId(mainStudent.username) ?: 0
		val feedBackResponse = feedBackApi.getFBMessage(mainStudent.username, fbToken.fbToken, maxId).redoAfterLogin(mainStudent) {
			feedBackApi.getFBMessage(mainStudent.username, fbToken.fbToken, maxId)
		}
		if (!feedBackResponse.isSuccessful) return false
		val list = feedBackResponse.fBMessages
		if (list.isNullOrEmpty()) {
			return false
		}
		val saveList = feedBackMessageDao.queryMessageForStudent(mainStudent.username, 0)
		list.forEach { message ->
			val s = saveList.find { it.id == message.id && it.createTime == message.createTime }
			if (s == null) {
				//没有找到，新增数据
				feedBackMessageDao.insert(message)
			} else {
				//找到了数据，更新
				s.content = message.content
				s.status = message.status
				s.platform = message.platform
				s.sender = message.sender
				s.receiver = message.receiver
				feedBackMessageDao.update(s)
			}
		}
		return saveList.any { it.status != 0 }.or(list.any { it.status != 0 })
	}

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
}