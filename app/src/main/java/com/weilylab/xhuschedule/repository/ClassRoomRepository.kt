package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.api.ClassRoomAPI
import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.utils.userDo.UserUtil
import com.weilylab.xhuschedule.viewmodel.QueryClassroomViewModel
import vip.mystery0.rx.DataManager
import vip.mystery0.rx.content
import vip.mystery0.rx.error
import vip.mystery0.rx.loading

object ClassRoomRepository {
	fun queryStudentList(queryTestViewModel: QueryClassroomViewModel) = StudentLocalDataSource.queryMainStudent(queryTestViewModel.student)

	fun queryClassroom(queryClassroomViewModel: QueryClassroomViewModel, index: Int = 0) {
		queryClassroomViewModel.classroomList.loading()
		DataManager.instance().doRequest(queryClassroomViewModel.classroomList) {
			val response = RetrofitFactory.retrofit
					.create(ClassRoomAPI::class.java)
					.getClassrooms(queryClassroomViewModel.student.value!!.data!!.username,
							queryClassroomViewModel.location.value!!,
							queryClassroomViewModel.week.value!!,
							queryClassroomViewModel.day.value!!,
							queryClassroomViewModel.time.value!!)
					.execute()
			if (response.isSuccessful) {
				val body = response.body()
				when {
					body == null -> queryClassroomViewModel.classroomList.error(Exception(StringConstant.hint_data_null))
					body.rt == ResponseCodeConstants.DONE -> queryClassroomViewModel.classroomList.content(body.classrooms)
					body.rt == ResponseCodeConstants.ERROR_NOT_LOGIN -> {
						if (index >= UserUtil.RETRY_TIME)
							queryClassroomViewModel.classroomList.error(Exception(StringConstant.hint_do_too_many))
						else
							UserUtil.login(queryClassroomViewModel.student.value!!.data!!, null, object : RequestListener<Boolean> {
								override fun done(t: Boolean) {
									queryClassroom(queryClassroomViewModel, index + 1)
								}

								override fun error(rt: String, msg: String?) {
									queryClassroomViewModel.classroomList.error(Exception(msg))
								}
							})
					}
					else -> queryClassroomViewModel.classroomList.error(Exception(body.msg))
				}
			} else {
				queryClassroomViewModel.classroomList.error(Exception(response.errorBody()?.string()))
			}
		}
	}
}