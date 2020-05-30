package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.api.FeedbackAPI
import com.weilylab.xhuschedule.model.FeedBackMessage
import com.weilylab.xhuschedule.model.FeedBackToken
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.module.redoAfterLogin
import com.weilylab.xhuschedule.repository.local.dao.FBTokenDao
import com.weilylab.xhuschedule.repository.local.dao.FeedBackMessageDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class FeedBackRepository : KoinComponent {
	private val fbTokenDao: FBTokenDao by inject()
	private val feedBackMessageDao: FeedBackMessageDao by inject()

	private val feedBackApi: FeedbackAPI by inject()

	private val studentRepository: StudentRepository by inject()

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
		val saveList = saveFeedbackMessageList(mainStudent, list)
		return saveList.any { it.status != 0 }.or(list.any { it.status != 0 })
	}

	private suspend fun saveFeedbackMessageList(mainStudent: Student, list: List<FeedBackMessage>): List<FeedBackMessage> {
		if (list.isNullOrEmpty()) {
			return emptyList()
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
		return saveList
	}

	suspend fun sendMessage(student: Student, maxId: Int, content: String): List<FeedBackMessage> {
		var fbToken = fbTokenDao.queryFeedBackTokenForUsername(student.username)
		if (fbToken == null) {
			studentRepository.doLoginOnly(student)
			fbToken = fbTokenDao.queryFeedBackTokenForUsername(student.username)
		}
		if (fbToken == null) {
			throw Exception("token请求失败")
		}
		val response = feedBackApi.sendFBMessage(student.username, fbToken.fbToken, content).redoAfterLogin(student) {
			feedBackApi.sendFBMessage(student.username, fbToken.fbToken, content)
		}
		if (!response.isSuccessful) {
			throw Exception(response.msg)
		}
		return getMessageFromLocal(student, maxId)
	}

	suspend fun getMessageFromLocal(student: Student, maxId: Int): List<FeedBackMessage> = feedBackMessageDao.queryMessageForStudent(student.username, maxId)

	suspend fun getMessageFromRemote(student: Student, maxId: Int): List<FeedBackMessage> {
		var fbToken = fbTokenDao.queryFeedBackTokenForUsername(student.username)
		if (fbToken == null) {
			studentRepository.doLoginOnly(student)
			fbToken = fbTokenDao.queryFeedBackTokenForUsername(student.username)
		}
		if (fbToken == null) {
			throw Exception("token请求失败")
		}
		val response = feedBackApi.getFBMessage(student.username, fbToken.fbToken, maxId).redoAfterLogin(student) {
			feedBackApi.getFBMessage(student.username, fbToken.fbToken, maxId)
		}
		if (!response.isSuccessful) {
			throw Exception(response.msg)
		}
		saveFeedbackMessageList(student, response.fBMessages)
		return getMessageFromLocal(student, maxId)
	}
}