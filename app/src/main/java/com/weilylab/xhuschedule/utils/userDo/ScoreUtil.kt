package com.weilylab.xhuschedule.utils.userDo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.weilylab.xhuschedule.api.ScoreAPI
import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.factory.GsonFactory
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.listener.DoSaveListener
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.CetScore
import com.weilylab.xhuschedule.model.ClassScore
import com.weilylab.xhuschedule.model.ExpScore
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.response.CetScoresResponse
import com.weilylab.xhuschedule.model.response.CetVCodeResponse
import com.weilylab.xhuschedule.model.response.ClassScoreResponse
import com.weilylab.xhuschedule.model.response.ExpScoreResponse
import vip.mystery0.rx.OnlyCompleteObserver
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
				.subscribeOn(Schedulers.io())
				.map {
					val scoreResponse = GsonFactory.parse<ClassScoreResponse>(it)
					if (scoreResponse.rt == ResponseCodeConstants.DONE) {
						val map = HashMap<Int, List<ClassScore>>()
						map[TYPE_SCORE] = scoreResponse.scores
						map[TYPE_FAILED] = scoreResponse.failScores
						doSaveListener?.doSave(map)
					}
					scoreResponse
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<ClassScoreResponse>() {
					override fun onFinish(data: ClassScoreResponse?) {
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

	fun getExpScore(student: Student, year: String, term: String, doSaveListener: DoSaveListener<List<ExpScore>>?, requestListener: RequestListener<List<ExpScore>>, index: Int = 0) {
		RetrofitFactory.retrofit
				.create(ScoreAPI::class.java)
				.getExpScores(student.username, year, term)
				.subscribeOn(Schedulers.io())
				.map {
					val scoreResponse = GsonFactory.parse<ExpScoreResponse>(it)
					if (scoreResponse.rt == ResponseCodeConstants.DONE)
						doSaveListener?.doSave(scoreResponse.expScores)
					scoreResponse
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<ExpScoreResponse>() {
					override fun onFinish(data: ExpScoreResponse?) {
						when {
							data == null -> requestListener.error(ResponseCodeConstants.UNKNOWN_ERROR, StringConstant.hint_data_null)
							data.rt == ResponseCodeConstants.DONE -> requestListener.done(data.expScores)
							data.rt == ResponseCodeConstants.ERROR_NOT_LOGIN -> {
								if (index == RETRY_TIME)
									requestListener.error(ResponseCodeConstants.DO_TOO_MANY, StringConstant.hint_do_too_many)
								else
									UserUtil.login(student, null, object : RequestListener<Boolean> {
										override fun done(t: Boolean) {
											getExpScore(student, year, term, doSaveListener, requestListener, index + 1)
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

	fun getCetVCode(student: Student, no: String, requestListener: RequestListener<Bitmap>, index: Int = 0) {
		RetrofitFactory.retrofit
				.create(ScoreAPI::class.java)
				.getCETVCode(student.username, no, null)
				.subscribeOn(Schedulers.io())
				.map { GsonFactory.parse<CetVCodeResponse>(it) }
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<CetVCodeResponse>() {
					override fun onFinish(data: CetVCodeResponse?) {
						when {
							data == null -> requestListener.error(ResponseCodeConstants.UNKNOWN_ERROR, StringConstant.hint_data_null)
							data.rt == ResponseCodeConstants.DONE -> {
								val bytes = Base64.decode(data.vcode.substring(data.vcode.indexOfFirst { it == ',' }), Base64.DEFAULT)
								requestListener.done(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
							}
							data.rt == ResponseCodeConstants.ERROR_NOT_LOGIN -> {
								if (index == RETRY_TIME)
									requestListener.error(ResponseCodeConstants.DO_TOO_MANY, StringConstant.hint_do_too_many)
								else
									UserUtil.login(student, null, object : RequestListener<Boolean> {
										override fun done(t: Boolean) {
											getCetVCode(student, no, requestListener, index + 1)
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

	fun getCetScores(student: Student, no: String, name: String, vcode: String, requestListener: RequestListener<CetScore>, index: Int = 0) {
		RetrofitFactory.retrofit
				.create(ScoreAPI::class.java)
				.getCETScores(student.username, no, name, vcode)
				.subscribeOn(Schedulers.io())
				.map { GsonFactory.parse<CetScoresResponse>(it) }
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<CetScoresResponse>() {
					override fun onFinish(data: CetScoresResponse?) {
						when {
							data == null -> requestListener.error(ResponseCodeConstants.UNKNOWN_ERROR, StringConstant.hint_data_null)
							data.rt == ResponseCodeConstants.DONE -> {
								requestListener.done(data.cetScore)
							}
							data.rt == ResponseCodeConstants.ERROR_NOT_LOGIN -> {
								if (index == RETRY_TIME)
									requestListener.error(ResponseCodeConstants.DO_TOO_MANY, StringConstant.hint_do_too_many)
								else
									UserUtil.login(student, null, object : RequestListener<Boolean> {
										override fun done(t: Boolean) {
											getCetScores(student, no, name, vcode, requestListener, index + 1)
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