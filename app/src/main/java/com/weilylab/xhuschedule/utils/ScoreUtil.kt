package com.weilylab.xhuschedule.utils

import com.weilylab.xhuschedule.api.ScoreAPI
import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.factory.GsonFactory
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.listener.DoSaveListener
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.ClassScore
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.response.ScoreResponse
import com.weilylab.xhuschedule.utils.rxAndroid.RxObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs

object ScoreUtil {
	private const val RETRY_TIME = 1
	const val TYPE_SCORE = 22
	const val TYPE_FAILED = 33

	fun getClassScore(student: Student, year: String, term: String, doSaveListener: DoSaveListener<Map<Int, List<ClassScore>>>?, requestListener: RequestListener<Map<Int, List<ClassScore>>>, index: Int = 0) {
		RetrofitFactory.retrofit
				.create(ScoreAPI::class.java)
				.getScores(student.username, year, term)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map {
					val scoreResponse = GsonFactory.parse<ScoreResponse>(it)
					if (scoreResponse.rt == ResponseCodeConstants.DONE) {
						val map = HashMap<Int, List<ClassScore>>()
						map[TYPE_SCORE] = scoreResponse.scores
						map[TYPE_FAILED] = scoreResponse.failScores
						doSaveListener?.doSave(map)
					}
					scoreResponse
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : RxObserver<ScoreResponse>() {
					override fun onFinish(data: ScoreResponse?) {
						when {
							data == null -> requestListener.error(ResponseCodeConstants.UNKNOWN_ERROR, StringConstant.hint_data_null)
							data.rt == ResponseCodeConstants.DONE -> {
								val map = HashMap<Int, List<ClassScore>>()
								map[TYPE_SCORE] = data.scores
								map[TYPE_FAILED] = data.failScores
								requestListener.done(map)
							}
							data.rt == ResponseCodeConstants.ERROR_NOT_LOGIN -> {
								if (index == RETRY_TIME)
									requestListener.error(ResponseCodeConstants.DO_TOO_MANY, StringConstant.hint_do_too_many)
								else
									UserUtil.login(student, null, object : RequestListener<Boolean> {
										override fun done(t: Boolean) {
											getClassScore(student, year, term, doSaveListener, requestListener, index + 1)
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
}