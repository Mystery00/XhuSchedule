/*
 * Created by Mystery0 on 17-11-27 下午9:53.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-27 下午9:53
 */

package com.weilylab.xhuschedule.classes

import android.content.Context
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.response.UploadLogResponse
import com.weilylab.xhuschedule.interfaces.CommonService
import com.weilylab.xhuschedule.listener.UploadLogListener
import com.weilylab.xhuschedule.util.ScheduleHelper
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.InputStreamReader
import java.io.Serializable

/**
 * Created by myste.
 */
data class XhuScheduleError(val time: String, val appVersionName: String,
                            val appVersionCode: Int, val AndroidVersion: String,
                            val sdk: Int, val vendor: String, val model: String,
                            val ex: Throwable) : Serializable {

    fun uploadLog(context: Context, logFile: File, listener: UploadLogListener) {
        val map = HashMap<String, String>()
        map.put("date", time)
        map.put("appName", context.getString(R.string.app_name))
        map.put("appVersionName", appVersionName)
        map.put("appVersionCode", appVersionCode.toString())
        map.put("androidVersion", AndroidVersion)
        map.put("sdk", sdk.toString())
        map.put("vendor", vendor)
        map.put("model", model)
        ScheduleHelper.phpRetrofit
                .create(CommonService::class.java)
                .uploadLog(createPartFromMap(map), prepareFilePart("logFile", logFile))
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), UploadLogResponse::class.java) }
                .subscribe(object : Observer<UploadLogResponse> {
                    private lateinit var response: UploadLogResponse
                    override fun onError(e: Throwable) {
                        listener.error(-1, e)
                    }

                    override fun onNext(t: UploadLogResponse) {
                        response = t
                    }

                    override fun onComplete() {
                        listener.done(response.code, response.message)
                    }

                    override fun onSubscribe(d: Disposable) {
                        listener.ready()
                    }
                })
    }

    /**
     * 从Map中构建请求体
     */
    private fun createPartFromMap(map: Map<String, String>): RequestBody {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        for (key in map.keys)
            builder.addFormDataPart(key, map[key]!!)
        return builder.build()
    }

    /**
     * 根据文件构建部分请求体，在Retrofit中会根据类型决定是否将part加入到请求体中
     */
    private fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
        val requestFile = RequestBody.create(MultipartBody.FORM, file)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
}