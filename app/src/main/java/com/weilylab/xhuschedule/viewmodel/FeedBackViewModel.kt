package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.model.FeedBackMessage
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.FeedBackRepository
import com.weilylab.xhuschedule.repository.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.content
import vip.mystery0.rx.empty
import vip.mystery0.rx.launch
import vip.mystery0.tools.ResourceException

class FeedBackViewModel : ViewModel(), KoinComponent {
	private val studentRepository: StudentRepository by inject()
	private val feedBackRepository: FeedBackRepository by inject()

	val mainStudent by lazy { MutableLiveData<Student>() }
	val feedBackMessageList by lazy { MutableLiveData<PackageData<List<FeedBackMessage>>>() }
	private val maxId by lazy { MutableLiveData<Int>() }
	private var job: Job? = null

	fun init() {
		launch(feedBackMessageList) {
			val student = studentRepository.queryMainStudent()
			if (student == null) {
				mainStudent.postValue(null)
				return@launch
			}
			mainStudent.postValue(student)
			queryMessageFromLocalInCoroutine(student)
		}
	}

	fun startReceiveMessage(student: Student) {
		job = launch(feedBackMessageList) {
			while (true) {
				val list = feedBackRepository.getMessageFromRemote(student, maxId.value ?: 0)
				if (list.isNullOrEmpty()) {
					feedBackMessageList.empty()
				} else {
					feedBackMessageList.content(list)
				}
				withContext(Dispatchers.Default) {
					Thread.sleep(30 * 1000)
				}
			}
		}
	}

	fun stopReceiveMessage() {
		job?.cancel()
	}

	fun getMessageFromLocal(student: Student) {
		launch(feedBackMessageList) {
			queryMessageFromLocalInCoroutine(student)
		}
	}

	private suspend fun queryMessageFromLocalInCoroutine(student: Student) {
		val list = feedBackRepository.getMessageFromLocal(student, maxId.value
				?: 0)
		maxId.postValue(list.maxBy { it.id }?.id)
		if (list.isNullOrEmpty()) {
			feedBackMessageList.empty()
		} else {
			feedBackMessageList.content(list)
		}
	}

	fun sendMessage(content: String) {
		launch(feedBackMessageList) {
			val student = mainStudent.value ?: throw ResourceException(R.string.hint_null_student)
			val list = feedBackMessageList.value?.data ?: ArrayList()
			val feedBackMessage = FeedBackMessage.newLoadingMessage(student.username, content)
			val loadingList = ArrayList<FeedBackMessage>()
			loadingList.addAll(list)
			loadingList.add(feedBackMessage)
			if (loadingList.isNullOrEmpty()) {
				feedBackMessageList.empty()
			} else {
				feedBackMessageList.content(loadingList)
			}
		}
	}
}