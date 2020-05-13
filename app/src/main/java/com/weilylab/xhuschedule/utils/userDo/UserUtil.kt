package com.weilylab.xhuschedule.utils.userDo

import com.weilylab.xhuschedule.api.UserAPI
import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.listener.DoSaveListener
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.model.response.GetUserDataResponse
import com.weilylab.xhuschedule.model.response.LoginResponse
import com.weilylab.xhuschedule.model.response.SetUserDataResponse
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import vip.mystery0.logs.Logs
import vip.mystery0.rx.OnlyCompleteObserver
import vip.mystery0.rx.Status.*
import vip.mystery0.tools.utils.NetworkTools

object UserUtil : KoinComponent {
	const val RETRY_TIME = 1

	fun login(student: Student, doSaveListener: DoSaveListener<Student>?, requestListener: RequestListener<Boolean>) {
		if (NetworkTools.instance.isConnectInternet())
			RetrofitFactory.retrofit
					.create(UserAPI::class.java)
					.autoLogin(student.username, student.password)
					.subscribeOn(Schedulers.io())
					.map {
						val data = it.fromJson<LoginResponse>()
						if (data.rt == ResponseCodeConstants.DONE) {
							StudentLocalDataSource.registerFeedBackToken(student, data.fbToken)
							doSaveListener?.doSave(student)
						}
						data
					}
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : OnlyCompleteObserver<LoginResponse>() {
						override fun onFinish(data: LoginResponse?) {
							when {
								data == null -> requestListener.error(ResponseCodeConstants.UNKNOWN_ERROR, StringConstant.hint_data_null)
								data.rt == ResponseCodeConstants.DONE -> requestListener.done(true)
								else -> requestListener.error(data.rt, data.msg)
							}
						}

						override fun onError(e: Throwable) {
							Logs.wtf("onError: ", e)
							requestListener.error(ResponseCodeConstants.CATCH_ERROR, e.message)
						}
					})
		else {
			requestListener.error(ResponseCodeConstants.CATCH_ERROR, StringConstant.hint_network_error)
		}
	}

	fun getInfo(student: Student, doSaveListener: DoSaveListener<StudentInfo>?, requestListener: RequestListener<StudentInfo>, index: Int = 0) {
		RetrofitFactory.retrofit
				.create(UserAPI::class.java)
				.getInfo(student.username)
				.subscribeOn(Schedulers.io())
				.map {
					val data = it.fromJson<StudentInfo>()
					if (data.rt == ResponseCodeConstants.DONE)
						doSaveListener?.doSave(data)
					data
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<StudentInfo>() {
					override fun onFinish(data: StudentInfo?) {
						when {
							data == null -> requestListener.error(ResponseCodeConstants.UNKNOWN_ERROR, StringConstant.hint_data_null)
							data.rt == ResponseCodeConstants.DONE -> requestListener.done(data)
							data.rt == ResponseCodeConstants.ERROR_NOT_LOGIN -> {
								if (index >= RETRY_TIME)
									requestListener.error(ResponseCodeConstants.DO_TOO_MANY, StringConstant.hint_do_too_many)
								else
									login(student, null, object : RequestListener<Boolean> {
										override fun done(t: Boolean) {
											getInfo(student, doSaveListener, requestListener, index + 1)
										}

										override fun error(rt: String, msg: String?) {
											requestListener.error(rt, msg)
										}
									})
							}
							else -> requestListener.error(data.rt, data.msg)
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						requestListener.error(ResponseCodeConstants.CATCH_ERROR, e.message)
					}
				})
	}

	fun findMainStudent(list: List<Student>?): Student? {
		if (list == null)
			return null
		val main = list.firstOrNull { it.isMain }
		if (main != null)
			return main
		if (list.isNotEmpty())
			return list[0]
		return null
	}

	fun checkStudentLogged(student: Student, listener: (Boolean) -> Unit) {
		StudentLocalDataSource.queryAllStudentList { packageData ->
			when (packageData.status) {
				Content -> {
					var isLogged = false
					packageData.data!!.forEach {
						if (it.username == student.username) {
							isLogged = true
							return@forEach
						}
					}
					listener.invoke(isLogged)
				}
				Empty -> listener.invoke(false)
				Error -> listener.invoke(false)
				Loading -> {
				}
			}
		}
	}

	/**
	 * 设置自定义数据
	 */
	fun setUserData(student: Student, key: String, value: String, requestListener: RequestListener<Boolean>, index: Int = 0) {
		RetrofitFactory.retrofit
				.create(UserAPI::class.java)
				.setUserData(student.username, key, value)
				.subscribeOn(Schedulers.io())
				.map { it.fromJson<SetUserDataResponse>() }
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<SetUserDataResponse>() {
					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						requestListener.error(ResponseCodeConstants.CATCH_ERROR, e.message)
					}

					override fun onFinish(data: SetUserDataResponse?) {
						when {
							data == null -> requestListener.error(ResponseCodeConstants.UNKNOWN_ERROR, StringConstant.hint_data_null)
							data.rt == ResponseCodeConstants.DONE -> requestListener.done(true)
							data.rt == ResponseCodeConstants.ERROR_NOT_LOGIN -> {
								if (index >= RETRY_TIME)
									requestListener.error(ResponseCodeConstants.DO_TOO_MANY, StringConstant.hint_do_too_many)
								else
									login(student, null, object : RequestListener<Boolean> {
										override fun done(t: Boolean) {
											setUserData(student, key, value, requestListener, index + 1)
										}

										override fun error(rt: String, msg: String?) {
											requestListener.error(rt, msg)
										}
									})
							}
							else -> requestListener.error(data.rt, data.msg)
						}
					}
				})
	}

	/**
	 * 设置自定义数据
	 */
	fun getUserData(student: Student, key: String, doSaveListener: DoSaveListener<GetUserDataResponse>?, requestListener: RequestListener<String>, index: Int = 0) {
		RetrofitFactory.retrofit
				.create(UserAPI::class.java)
				.getUserData(student.username, key)
				.subscribeOn(Schedulers.io())
				.map {
					val data = it.fromJson<GetUserDataResponse>()
					if (data.rt == ResponseCodeConstants.DONE)
						doSaveListener?.doSave(data)
					data
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<GetUserDataResponse>() {
					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						requestListener.error(ResponseCodeConstants.CATCH_ERROR, e.message)
					}

					override fun onFinish(data: GetUserDataResponse?) {
						when {
							data == null -> requestListener.error(ResponseCodeConstants.UNKNOWN_ERROR, StringConstant.hint_data_null)
							data.rt == ResponseCodeConstants.DONE -> requestListener.done(data.value)
							data.rt == ResponseCodeConstants.ERROR_NOT_LOGIN -> {
								if (index >= RETRY_TIME)
									requestListener.error(ResponseCodeConstants.DO_TOO_MANY, StringConstant.hint_do_too_many)
								else
									login(student, null, object : RequestListener<Boolean> {
										override fun done(t: Boolean) {
											getUserData(student, key, doSaveListener, requestListener, index + 1)
										}

										override fun error(rt: String, msg: String?) {
											requestListener.error(rt, msg)
										}
									})
							}
							else -> requestListener.error(data.rt, data.msg)
						}
					}
				})
	}
}